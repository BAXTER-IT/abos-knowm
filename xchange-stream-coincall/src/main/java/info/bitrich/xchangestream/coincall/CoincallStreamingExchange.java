package info.bitrich.xchangestream.coincall;

import info.bitrich.xchangestream.coincall.service.CoincallStreamingAccountService;
import info.bitrich.xchangestream.coincall.service.CoincallStreamingTradeService;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.service.netty.ConnectionStateModel;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.coincall.CoincallExchange;

public class CoincallStreamingExchange extends CoincallExchange implements StreamingExchange {

  CoincallStreamingService spotStreamingService;
  CoincallStreamingService futuresStreamingService;
  CoincallStreamingService optionsStreamingService;
  private CoincallStreamingTradeService streamingTradeService;
  private CoincallStreamingAccountService streamingAccountService;

  @Override
  public Completable connect(ProductSubscription... args) {

    spotStreamingService =
        new CoincallStreamingService(
            getCoincallSpotURI(), exchangeSpecification);
    futuresStreamingService =
        new CoincallStreamingService(
            (String) exchangeSpecification.getExchangeSpecificParametersItem(WS_FUTURES_URI),
            exchangeSpecification);
    optionsStreamingService =
        new CoincallStreamingService(
            (String) exchangeSpecification.getExchangeSpecificParametersItem(WS_OPTIONS_URI),
            exchangeSpecification);

    streamingTradeService = new CoincallStreamingTradeService(spotStreamingService);
    streamingAccountService = new CoincallStreamingAccountService(futuresStreamingService,
        optionsStreamingService);

    return Completable.mergeArray(
        spotStreamingService.connect(),
        futuresStreamingService.connect(),
        optionsStreamingService.connect()
    );
  }

  private String getCoincallSpotURI() {
    return (String) (exchangeSpecification.getSecretKey() == null
        ? exchangeSpecification.getExchangeSpecificParametersItem(WS_SPOT_PUBLIC_URI)
        : exchangeSpecification.getExchangeSpecificParametersItem(WS_SPOT_PRIVATE_URI));
  }

  @Override
  public boolean isAlive() {
    return spotStreamingService != null && spotStreamingService.isSocketOpen();
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    spotStreamingService.useCompressedMessages(compressedMessages);
    futuresStreamingService.useCompressedMessages(compressedMessages);
    optionsStreamingService.useCompressedMessages(compressedMessages);
  }

  @Override
  public Completable disconnect() {
    spotStreamingService.pingPongDisconnectIfConnected();
    return spotStreamingService.disconnect();
  }

  @Override
  public Observable<Object> connectionSuccess() {
    return spotStreamingService.subscribeConnectionSuccess();
  }

  @Override
  public Observable<Object> disconnectObservable() {
    return spotStreamingService.subscribeDisconnect();
  }

  @Override
  public Observable<Throwable> reconnectFailure() {
    return spotStreamingService.subscribeReconnectFailure();
  }

  @Override
  public Observable<ConnectionStateModel.State> connectionStateObservable() {
    return spotStreamingService.subscribeConnectionState();
  }

  @Override
  public void resubscribeChannels() {
    spotStreamingService.resubscribeChannels();
  }

  @Override
  public CoincallStreamingTradeService getStreamingTradeService() {
    return this.streamingTradeService;
  }

  @Override
  public CoincallStreamingAccountService getStreamingAccountService() {
    return this.streamingAccountService;
  }
}
