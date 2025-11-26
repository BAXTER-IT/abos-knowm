package info.bitrich.xchangestream.bybit;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.service.netty.ConnectionStateModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import lombok.Getter;

import org.knowm.xchange.bybit.BybitExchange;

@Getter
public class BybitStreamingExchange extends BybitExchange implements StreamingExchange {

  BybitStreamingService streamingService;
  private BybitStreamingTradeService streamingTradeService;
  private BybitStreamingAccountService streamingAccountService;

  @Override
  protected void initServices() {
    super.initServices();
    streamingService =
        new BybitStreamingService(
            getBybitURI(useSandbox(exchangeSpecification), true, ""), exchangeSpecification);

    streamingTradeService = new BybitStreamingTradeService(streamingService);
  }

  @Override
  public Completable connect(ProductSubscription... args) {

    if (exchangeSpecification.getApiKey() != null) {
      streamingService =
          new BybitStreamingService(
              getBybitURI(useSandbox(exchangeSpecification), true, ""), exchangeSpecification);
      streamingTradeService = new BybitStreamingTradeService(streamingService);
      streamingAccountService = new BybitStreamingAccountService(streamingService);
    } else {
      streamingService =
          new BybitStreamingService(
              getBybitURI(
                  useSandbox(exchangeSpecification),
                  false,
                  MarketType.SPOT.toString().toLowerCase()),
              exchangeSpecification);
    }

    return streamingService.connect();
  }

  private String getBybitURI(boolean isSandBox, boolean isAuthenticated, String marketType) {
    return "wss://stream"
        + (isSandBox ? "-testnet" : "")
        + ".bybit.com/v5/"
        + ((isAuthenticated) ? "private" : "public/" + marketType);
  }

  @Override
  public boolean isAlive() {
    return streamingService != null && streamingService.isSocketOpen();
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

  private enum MarketType {
    SPOT,
    LINEAR,
    INVERSE,
    OPTION
  }
}
