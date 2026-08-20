package info.bitrich.xchangestream.deribit;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.deribit.config.Config;
import io.reactivex.rxjava3.core.Completable;
import lombok.Getter;
import org.knowm.xchange.deribit.v2.DeribitExchange;

@Getter
public class DeribitStreamingExchange extends DeribitExchange implements StreamingExchange {

  private DeribitPrivateStreamingService privateStreamingService;
  private StreamingTradeService streamingTradeService;

  /**
   * Opens the private (execution-report) socket only. Deribit market data is deliberately not
   * served by this connector — it is consumed elsewhere in the stack, never from here.
   */
  @Override
  public Completable connect(ProductSubscription... args) {
    privateStreamingService = createPrivateStreamingService(getWsUrl());
    applyStreamingSpecification(exchangeSpecification, privateStreamingService);
    streamingTradeService = new DeribitStreamingTradeService(privateStreamingService);
    return privateStreamingService.connect();
  }

  /** Test seam: lets a test substitute a recording private service. */
  DeribitPrivateStreamingService createPrivateStreamingService(String wsUrl) {
    return new DeribitPrivateStreamingService(wsUrl, exchangeSpecification.getApiKey(), exchangeSpecification.getSecretKey());
  }

  /**
   * The WebSocket address to connect to: the exchange specification's override or WS endpoint when
   * either is set, otherwise the hardcoded production address.
   */
  String getWsUrl() {
    String override = exchangeSpecification.getOverrideWebsocketApiUri();
    if (override != null && !override.isBlank()) {
      return override;
    }
    String wsEndpoint = exchangeSpecification.getWsEndpoint();
    if (wsEndpoint != null && !wsEndpoint.isBlank()) {
      return wsEndpoint;
    }
    return Config.V2_WS_URL;
  }

  @Override
  public Completable disconnect() {
    // Deferred so the socket is forgotten only when the close actually runs, not at call time.
    return Completable.defer(() -> {
      DeribitPrivateStreamingService service = privateStreamingService;
      privateStreamingService = null;
      streamingTradeService = null;
      return service == null ? Completable.complete() : service.disconnect();
    });
  }

  @Override
  public boolean isAlive() {
    return privateStreamingService != null && privateStreamingService.isSocketOpen();
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    privateStreamingService.useCompressedMessages(compressedMessages);
  }
}
