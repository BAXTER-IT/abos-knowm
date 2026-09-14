package org.knowm.xchange.coincall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coincall.services.CoincallAccountService;
import org.knowm.xchange.coincall.services.CoincallMarketDataService;
import org.knowm.xchange.coincall.services.CoincallTradeService;

public class CoincallExchangeTest {

  @Test
  public void testDefaultExchangeSpecification_basicFields() {
    // arrange
    CoincallExchange exchange = new CoincallExchange();

    // act
    ExchangeSpecification spec = exchange.getDefaultExchangeSpecification();

    // assert
    assertNotNull("ExchangeSpecification should not be null", spec);
    assertEquals("https://api.coincall.com", spec.getSslUri());
    assertEquals("api.coincall.com", spec.getHost());
    assertEquals("Coincall", spec.getExchangeName());
  }

  @Test
  public void testDefaultExchangeSpecification_websocketEndpoints() {
    // arrange
    CoincallExchange exchange = new CoincallExchange();

    // act
    ExchangeSpecification spec = exchange.getDefaultExchangeSpecification();

    // assert
    // default ws endpoint
    assertEquals("wss://ws.coincall.com/futures", spec.getWsEndpoint());

    // exchange-specific params
    assertEquals(
        "wss://ws.coincall.com/options",
        spec.getExchangeSpecificParametersItem(CoincallExchange.WS_OPTIONS_URI)
    );
    assertEquals(
        "wss://ws.coincall.com/futures",
        spec.getExchangeSpecificParametersItem(CoincallExchange.WS_FUTURES_URI)
    );
    assertEquals(
        "wss://ws.coincall.com/spot/ws",
        spec.getExchangeSpecificParametersItem(CoincallExchange.WS_SPOT_PUBLIC_URI)
    );
    assertEquals(
        "wss://ws.coincall.com/spot/ws/private",
        spec.getExchangeSpecificParametersItem(CoincallExchange.WS_SPOT_PRIVATE_URI)
    );
  }

  @Test
  public void testDefaultExchangeSpecification_shouldNotLoadRemoteMetadata() {
    // arrange
    CoincallExchange exchange = new CoincallExchange();

    // act
    ExchangeSpecification spec = exchange.getDefaultExchangeSpecification();

  }

  @Test
  public void testInitServices_createsCorrectServiceImplementations() {
    // arrange
    CoincallExchange exchange = new CoincallExchange();
    // BaseExchange usually expects applySpecification to trigger initServices
    ExchangeSpecification spec = exchange.getDefaultExchangeSpecification();
    spec.setApiKey("test-api-key");
    spec.setSecretKey("test-secret-key");
    // act
    exchange.applySpecification(spec);

    // assert
    assertNotNull(exchange.getAccountService());
    assertNotNull(exchange.getMarketDataService());
    assertNotNull(exchange.getTradeService());

    assertTrue(
        "AccountService should be CoincallAccountService",
        exchange.getAccountService() instanceof CoincallAccountService
    );
    assertTrue(
        "MarketDataService should be CoincallMarketDataService",
        exchange.getMarketDataService() instanceof CoincallMarketDataService
    );
    assertTrue(
        "TradeService should be CoincallTradeService",
        exchange.getTradeService() instanceof CoincallTradeService
    );
  }

}