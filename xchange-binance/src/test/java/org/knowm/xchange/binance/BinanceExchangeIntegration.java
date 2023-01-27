package org.knowm.xchange.binance;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.dto.meta.BinanceSystemStatus;
import org.knowm.xchange.binance.service.BinanceAccountService;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class BinanceExchangeIntegration {
  protected static BinanceExchange exchange;

  private static int counter = 8080;

  @Rule public WireMockRule wireMockRule = new WireMockRule(++counter);

  @BeforeClass
  public static void beforeClass() throws Exception {
    createExchange();
  }

  @Test
  public void testSytemStatus() throws IOException {
    assumeProduction();
    BinanceSystemStatus systemStatus =
        ((BinanceAccountService) exchange.getAccountService()).getSystemStatus();
    assertThat(systemStatus).isNotNull();
    assertThat(systemStatus.getStatus()).isNotEmpty();
  }

  protected static void createExchange() throws Exception {
    exchange = ExchangeFactory.INSTANCE.createExchangeWithoutSpecification(BinanceExchange.class);
    ExchangeSpecification spec = exchange.getDefaultExchangeSpecification();
    boolean useSandbox =
        Boolean.parseBoolean(
            System.getProperty(Exchange.USE_SANDBOX, Boolean.FALSE.toString()));
    spec.setExchangeSpecificParametersItem(Exchange.USE_SANDBOX, useSandbox);
    exchange.applySpecification(spec);
  }

  protected void assumeProduction() {
    Assume.assumeFalse("Using sandbox", exchange.usingSandbox());
  }

  protected BinanceExchange createExchangeMocked() {
    BinanceExchange exchangeMocked =
        ExchangeFactory.INSTANCE.createExchangeWithoutSpecification(BinanceExchange.class);
    ExchangeSpecification specification = exchangeMocked.getDefaultExchangeSpecification();
    specification.setHost("localhost");
    specification.setSslUri("http://localhost:" + wireMockRule.port() + "/");
    specification.setPort(wireMockRule.port());
    specification.setShouldLoadRemoteMetaData(false);
    exchangeMocked.applySpecification(specification);
    return exchangeMocked;
  }
}
