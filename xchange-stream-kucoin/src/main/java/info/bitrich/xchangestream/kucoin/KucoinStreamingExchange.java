package info.bitrich.xchangestream.kucoin;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.service.netty.NettyStreamingService;
import info.bitrich.xchangestream.util.Events;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.knowm.xchange.kucoin.KucoinExchange;
import org.knowm.xchange.kucoin.dto.response.WebsocketResponse;

public class KucoinStreamingExchange extends KucoinExchange implements StreamingExchange {

  private KucoinStreamingService publicStreamingService;
  private KucoinStreamingService privateStreamingService;
  private KucoinStreamingMarketDataService streamingMarketDataService;
  private KucoinStreamingTradeService streamingTradeService;
  private KucoinStreamingAccountService streamingAccountService;

  private final List<NettyStreamingService<?>> services = new CopyOnWriteArrayList<>();
  private Runnable onApiCall;

  /**
   * ABOS hub #908: how long one websocket connect may take. Netty's own timeout covers only opening
   * the socket, not the websocket handshake, so an unanswered handshake used to wait forever on the
   * caller's thread (2026-10-16: one account had no fills for 4 h 20 min).
   */
  static final long CONNECT_LIMIT_MS = 30_000L;

  @Override
  protected void initServices() {
    super.initServices();
    this.onApiCall = Events.onApiCall(exchangeSpecification);
  }

  @Override
  public Completable connect(ProductSubscription... args) {
    ProductSubscription subscriptions = args[0];

    Completable complete = Completable.complete();
    services.clear();

    if (subscriptions.hasUnauthenticated()) {
      complete =
          complete.doOnComplete(
              () -> {
                WebsocketResponse connectionDetails = getPublicWebsocketConnectionDetails();
                WebsocketResponse.InstanceServer instanceServer =
                    connectionDetails.getInstanceServers().get(0);
                String url =
                    instanceServer.getEndpoint() + "?token=" + connectionDetails.getToken();

                publicStreamingService =
                    new KucoinStreamingService(url, instanceServer.getPingInterval(), false);
                applyStreamingSpecification(getExchangeSpecification(), publicStreamingService);
                connectService(publicStreamingService, "public", CONNECT_LIMIT_MS);
                streamingMarketDataService =
                    new KucoinStreamingMarketDataService(
                        publicStreamingService, getMarketDataService(), onApiCall);
              });
    }

    if (subscriptions.hasAuthenticated()) {
      if (exchangeSpecification.getApiKey() == null) {
        throw new IllegalArgumentException("API key required for authenticated streams");
      }

      complete =
          complete.doOnComplete(
              () -> {
                WebsocketResponse connectionDetails = getPrivateWebsocketConnectionDetails();
                WebsocketResponse.InstanceServer instanceServer =
                    connectionDetails.getInstanceServers().get(0);
                String url =
                    instanceServer.getEndpoint() + "?token=" + connectionDetails.getToken();

                privateStreamingService =
                    new KucoinStreamingService(url, instanceServer.getPingInterval(), true);
                applyStreamingSpecification(getExchangeSpecification(), privateStreamingService);
                connectService(privateStreamingService, "private", CONNECT_LIMIT_MS);
                streamingTradeService = new KucoinStreamingTradeService(privateStreamingService);
                streamingAccountService = new KucoinStreamingAccountService(privateStreamingService);
              });
    }

    return complete;
  }

  /**
   * ABOS hub #908: connects one websocket within a time limit. The service is recorded first, so
   * {@link #disconnect()} can always reach it. On a failure or a timeout its automatic reconnect is
   * switched off, it is closed and dropped, and the error is thrown, so a failed attempt never keeps
   * reconnecting on its own threads.
   */
  void connectService(KucoinStreamingService service, String which, long limitMs) {
    services.add(service);
    try {
      if (!service.connect().blockingAwait(limitMs, TimeUnit.MILLISECONDS)) {
        throw new IllegalStateException(
            "KuCoin " + which + " websocket did not connect within " + limitMs / 1000 + " s");
      }
    } catch (RuntimeException | Error e) {
      logger.warn("encountered error while connecting the {} websocket: {}", which, e.toString());
      // Off before closing; the close also shuts the service's threads down, cancelling a queued redial.
      service.setAutoReconnect(false);
      services.remove(service);
      try {
        if (!service.disconnect().blockingAwait(limitMs, TimeUnit.MILLISECONDS)) {
          logger.warn("closing the failed {} websocket did not finish within {} s", which, limitMs / 1000);
        }
      } catch (RuntimeException closeFailure) {
        logger.warn("closing the failed {} websocket failed: {}", which, closeFailure.toString());
      }
      throw e;
    }
  }

  @Override
  public Completable disconnect() {

    if (publicStreamingService != null) {
      publicStreamingService = null;
      streamingMarketDataService = null;
    }

    if (privateStreamingService != null) {
      privateStreamingService = null;
    }

    List<Completable> completables =
        services.stream().map(NettyStreamingService::disconnect).collect(Collectors.toList());
    services.clear();
    return Completable.concat(completables);
  }

  @Override
  public boolean isAlive() {
    return services.stream().anyMatch(NettyStreamingService::isSocketOpen);
  }

  @Override
  public Observable<Throwable> reconnectFailure() {
    return Observable.concat(
        services.stream()
            .map(NettyStreamingService::subscribeReconnectFailure)
            .collect(Collectors.toList()));
  }

  @Override
  public Observable<Object> connectionSuccess() {
    return Observable.concat(
        services.stream()
            .map(NettyStreamingService::subscribeConnectionSuccess)
            .collect(Collectors.toList()));
  }

  @Override
  public KucoinStreamingMarketDataService getStreamingMarketDataService() {
    return streamingMarketDataService;
  }

  @Override
  public KucoinStreamingAccountService getStreamingAccountService() {
    return streamingAccountService;
  }

  @Override
  public StreamingTradeService getStreamingTradeService() {
    return streamingTradeService;
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    services.forEach(s -> s.useCompressedMessages(compressedMessages));
  }
}
