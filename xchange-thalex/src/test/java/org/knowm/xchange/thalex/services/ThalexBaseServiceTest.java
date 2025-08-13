package org.knowm.xchange.thalex.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.thalex.ThalexExchange;

public class ThalexBaseServiceTest {

  @Test
  public void testInitialization() {

    ThalexExchange exchange = new ThalexExchange() {
      protected void initServices() {
        accountService = new ThalexAccountService(this);
        marketDataService = new ThalexMarketDataService(this);
        tradeService = new ThalexTradeService(this);
      }
    };

    ExchangeSpecification exchangeSpecification = exchange.getDefaultExchangeSpecification();
    exchangeSpecification.setApiKey("test-api-key");
    exchange.applySpecification(exchangeSpecification);

    ThalexBaseService thalexBaseService = new ThalexBaseService(exchange);
    assertNotNull(thalexBaseService.thalex);
    assertNotNull(thalexBaseService.thalexDigest);
    assertEquals("test-api-key", thalexBaseService.apiKey);

  }
}