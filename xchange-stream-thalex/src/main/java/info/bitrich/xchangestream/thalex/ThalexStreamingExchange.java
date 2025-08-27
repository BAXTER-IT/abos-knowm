package info.bitrich.xchangestream.thalex;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.service.netty.ConnectionStateModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.thalex.ThalexExchange;

@Slf4j
public class ThalexStreamingExchange extends ThalexExchange
    implements StreamingExchange {

  ThalexStreamingService streamingService;
  private ThalexStreamingTradeService streamingTradeService;

  @Override
  public Completable connect(ProductSubscription... args) {

    if (!isAuthorized()) {
      logger.error("API key pair required for connecting to FineryMarkets streaming API");
      return Completable.error(
          new IllegalStateException(
              "API key pair required for connecting to FineryMarkets streaming API"));
    }
    streamingService =
        new ThalexStreamingService(
            exchangeSpecification.getWsEndpoint(), exchangeSpecification);
    streamingTradeService = new ThalexStreamingTradeService(streamingService);

    return streamingService.connect();
  }

  private boolean isAuthorized() {
    return exchangeSpecification.getApiKey() != null
        && exchangeSpecification.getSecretKey() != null;
  }

  @Override
  public boolean isAlive() {
    return streamingService != null && streamingService.isSocketOpen();
  }

  @Override
  public ThalexStreamingTradeService getStreamingTradeService() {
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
