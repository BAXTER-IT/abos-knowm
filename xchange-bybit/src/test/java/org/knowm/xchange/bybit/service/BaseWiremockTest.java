package org.knowm.xchange.bybit.service;

import java.io.IOException;

import org.junit.Rule;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bybit.BybitExchange;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class BaseWiremockTest {

    private static int counter = 8080;
  
    @Rule public WireMockRule wireMockRule = new WireMockRule(++counter);

    public Exchange createExchange() throws IOException {
        Exchange exchange =
                ExchangeFactory.INSTANCE.createExchangeWithoutSpecification(BybitExchange.class);
        ExchangeSpecification specification = exchange.getDefaultExchangeSpecification();
        specification.setHost("localhost");
        specification.setSslUri("http://localhost:" + wireMockRule.port());
        specification.setPort(wireMockRule.port());
        specification.setApiKey("test_api_key");
        specification.setSecretKey("test_secret_key");
        specification.setShouldLoadRemoteMetaData(false);
        exchange.applySpecification(specification);
        return exchange;
    }
}
