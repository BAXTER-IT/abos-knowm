package info.bitrich.xchangestream.deribit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import info.bitrich.xchangestream.deribit.config.Config;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.junit.jupiter.api.BeforeEach;

class DeribitStreamingExchangeTest {

  private DeribitStreamingExchange exchange;
  private ExchangeSpecification specification;

  @BeforeEach
  void setUp() {
    exchange = new DeribitStreamingExchange();
    specification = exchange.getDefaultExchangeSpecification();
    specification.setShouldLoadRemoteMetaData(false);
  }

  @Test
  void usesTheProductionAddressWhenTheSpecificationSetsNone() {
    exchange.applySpecification(specification);

    assertEquals(Config.V2_WS_URL, exchange.getWsUrl());
  }

  @Test
  void usesTheWebsocketOverrideFromTheSpecification() {
    specification.setOverrideWebsocketApiUri("ws://127.0.0.1:9100/test-exchange/deribit");
    exchange.applySpecification(specification);

    assertEquals("ws://127.0.0.1:9100/test-exchange/deribit", exchange.getWsUrl());
  }

  @Test
  void usesTheWsEndpointFromTheSpecificationWhenNoOverrideIsSet() {
    specification.setWsEndpoint("ws://127.0.0.1:9100/test-exchange/deribit");
    exchange.applySpecification(specification);

    assertEquals("ws://127.0.0.1:9100/test-exchange/deribit", exchange.getWsUrl());
  }

  @Test
  void prefersTheOverrideOverTheWsEndpoint() {
    specification.setOverrideWebsocketApiUri("ws://127.0.0.1:9100/override");
    specification.setWsEndpoint("ws://127.0.0.1:9100/ws-endpoint");
    exchange.applySpecification(specification);

    assertEquals("ws://127.0.0.1:9100/override", exchange.getWsUrl());
  }
}
