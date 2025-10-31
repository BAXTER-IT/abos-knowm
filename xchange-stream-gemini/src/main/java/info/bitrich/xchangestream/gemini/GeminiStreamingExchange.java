package info.bitrich.xchangestream.gemini;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.service.netty.ConnectionStateModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.gemini.GeminiExchange;

@Slf4j
public class GeminiStreamingExchange extends GeminiExchange
    implements StreamingExchange {

  private static final String URL_FILL_EVENTS = "/v1/order/events?eventTypeFilter=fill";
  GeminiStreamingService streamingService;
  private GeminiStreamingTradeService streamingTradeService;

  @Override
  public Completable connect(ProductSubscription... args) {

    if (!isAuthorized()) {
      logger.error("API key pair required for connecting to Gemini streaming API");
      return Completable.error(
          new IllegalStateException(
              "API key pair required for connecting to Gemini streaming API"));
    }
    String wsEndpoint = exchangeSpecification.getWsEndpoint() + URL_FILL_EVENTS;
    streamingService =
        new GeminiStreamingService(wsEndpoint, exchangeSpecification);
    streamingTradeService = new GeminiStreamingTradeService(streamingService);

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
  public GeminiStreamingTradeService getStreamingTradeService() {
    return streamingTradeService;
  }

  @Override
  public Completable disconnect() {
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
