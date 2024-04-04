package info.bitrich.xchangestream.finerymarkets;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.service.netty.ConnectionStateModel;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.finerymarkets.FineryMarketsExchange;

public class FineryMarketsStreamingExchange extends FineryMarketsExchange
    implements StreamingExchange {

  FineryMarketsStreamingService streamingService;
  private FineryMarketsStreamingTradeService streamingTradeService;

  //  @Override
  //  protected void initServices() {
  //    super.initServices();
  //    streamingService =
  //        new FineryMarketsStreamingService(
  //            getFineryMarketsURI(useSandbox(exchangeSpecification), true, ""),
  // exchangeSpecification);
  //
  //    streamingTradeService = new FineryMarketsStreamingTradeService(streamingService);
  //  }

  @Override
  public Completable connect(ProductSubscription... args) {

    if (!isAuthorized()) {
      logger.error("API key pair required for connecting to FineryMarkets streaming API");
      return Completable.error(
          new IllegalStateException(
              "API key pair required for connecting to FineryMarkets streaming API"));
    }
    streamingService =
        new FineryMarketsStreamingService(
            exchangeSpecification.getWsEndpoint(), exchangeSpecification);
    streamingTradeService = new FineryMarketsStreamingTradeService(streamingService);

    return streamingService.connect();
  }

  private boolean isAuthorized() {
    return exchangeSpecification.getApiKey() != null && exchangeSpecification.getSecretKey() != null;
  }

  @Override
  public boolean isAlive() {
    return streamingService != null && streamingService.isSocketOpen();
  }

  @Override
  public FineryMarketsStreamingTradeService getStreamingTradeService() {
    return streamingTradeService;
  }

  @Override
  public Completable disconnect() {
    streamingService.pingPongDisconnectIfConnected();
    return streamingService.disconnect();
  }

  @Override
  public Observable<Object> connectionSuccess() {
    return streamingService.subscribeConnectionSuccess();
  }

  @Override
  public Observable<Object> disconnectObservable() {
    return streamingService.subscribeDisconnect();
  }

  @Override
  public Observable<Throwable> reconnectFailure() {
    return streamingService.subscribeReconnectFailure();
  }

  @Override
  public Observable<ConnectionStateModel.State> connectionStateObservable() {
    return streamingService.subscribeConnectionState();
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    streamingService.useCompressedMessages(compressedMessages);
  }

  @Override
  public void resubscribeChannels() {
    streamingService.resubscribeChannels();
  }
}
