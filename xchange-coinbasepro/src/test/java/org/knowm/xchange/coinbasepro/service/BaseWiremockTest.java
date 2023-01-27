package org.knowm.xchange.coinbasepro.service;

import org.junit.Rule;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coinbasepro.CoinbaseProExchange;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class BaseWiremockTest {

  private static int counter = 8080;

  @Rule public WireMockRule wireMockRule = new WireMockRule(++counter);
  public static final String WIREMOCK_FILES_PATH = "__files";

  public Exchange createExchange() {
    Exchange exchange =
        ExchangeFactory.INSTANCE.createExchangeWithoutSpecification(CoinbaseProExchange.class);
    ExchangeSpecification specification = exchange.getDefaultExchangeSpecification();
    specification.setHost("localhost");
    specification.setSslUri("http://localhost:" + wireMockRule.port());
    specification.setPort(wireMockRule.port());
    specification.setShouldLoadRemoteMetaData(false);
    exchange.applySpecification(specification);
    return exchange;
  }
}
