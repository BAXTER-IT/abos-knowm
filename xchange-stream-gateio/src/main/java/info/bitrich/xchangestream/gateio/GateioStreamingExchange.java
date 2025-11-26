package info.bitrich.xchangestream.gateio;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.gateio.config.Config;
import io.reactivex.Completable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.gateio.GateioExchange;

public class GateioStreamingExchange extends GateioExchange implements StreamingExchange {

  private GateioStreamingService streamingService;
  private GateioStreamingService perpetualFuturesStreamingService;
  private GateioStreamingService deliveryFuturesStreamingService;
  private StreamingMarketDataService streamingMarketDataService;
  private StreamingTradeService streamingTradeService;
  private StreamingAccountService streamingAccountService;

  public GateioStreamingExchange() {
  }

  @Override
  public Completable connect(ProductSubscription... args) {
    streamingService = new GateioStreamingService(exchangeSpecification.getSslUri(),
        exchangeSpecification.getApiKey(), exchangeSpecification.getSecretKey());
    applyStreamingSpecification(exchangeSpecification, streamingService);
    perpetualFuturesStreamingService = new GateioStreamingService(
        Config.WS_URL_V4_PERPETUAL_FUTURES_USDT, exchangeSpecification.getApiKey(),
        exchangeSpecification.getSecretKey());
    applyStreamingSpecification(exchangeSpecification, perpetualFuturesStreamingService);
    deliveryFuturesStreamingService = new GateioStreamingService(
        Config.WS_URL_V4_DELIVERY_FUTURES_USDT, exchangeSpecification.getApiKey(),
        exchangeSpecification.getSecretKey());
    applyStreamingSpecification(exchangeSpecification, deliveryFuturesStreamingService);
    streamingMarketDataService = new GateioStreamingMarketDataService(streamingService);
    streamingTradeService = new GateioStreamingTradeService(streamingService);
    streamingAccountService = new GateioStreamingAccountService(streamingService, perpetualFuturesStreamingService, deliveryFuturesStreamingService);
    return Completable.mergeArray(
        streamingService.connect(),
        perpetualFuturesStreamingService.connect(),
        deliveryFuturesStreamingService.connect()
    ).retry();
  }

  @Override
  public Completable disconnect() {
    GateioStreamingService service = streamingService;
    GateioStreamingService perpetualFuturesService = perpetualFuturesStreamingService;
    GateioStreamingService deliveryFuturesService = deliveryFuturesStreamingService;
    streamingService = null;
    perpetualFuturesStreamingService = null;
    deliveryFuturesStreamingService = null;
    streamingMarketDataService = null;
    streamingTradeService = null;
    streamingAccountService = null;
    return Completable.mergeArray(
        service.disconnect(),
        perpetualFuturesService.disconnect(),
        deliveryFuturesService.disconnect()
    );
  }

  @Override
  public StreamingMarketDataService getStreamingMarketDataService() {
    return streamingMarketDataService;
  }

  @Override
  public StreamingTradeService getStreamingTradeService() {
    return streamingTradeService;
  }

  @Override
  public StreamingAccountService getStreamingAccountService() {
    return streamingAccountService;
  }

  @Override
  public boolean isAlive() {
    return streamingService != null && streamingService.isSocketOpen();
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    streamingService.useCompressedMessages(compressedMessages);
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {
    ExchangeSpecification specification = super.getDefaultExchangeSpecification();
    specification.setShouldLoadRemoteMetaData(false);
    specification.setSslUri(Config.WS_URL_V4_SPOT);
    return specification;
  }
}
