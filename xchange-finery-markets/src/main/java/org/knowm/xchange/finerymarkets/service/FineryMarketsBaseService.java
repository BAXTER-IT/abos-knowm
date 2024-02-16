package org.knowm.xchange.finerymarkets.service;

import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.client.ExchangeRestProxyBuilder;
import org.knowm.xchange.client.ProxyConfig;
import org.knowm.xchange.finerymarkets.FineryMarketsAuthenticated;
import org.knowm.xchange.service.BaseService;
import org.knowm.xchange.utils.nonce.CurrentTimeIncrementalNonceFactory;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.SynchronizedValueFactory;

public class FineryMarketsBaseService implements BaseService {

  protected final FineryMarketsAuthenticated fineryMarketsAuthenticated;
  protected final ParamsDigest signatureCreator;
  protected final SynchronizedValueFactory<Long> nonceFactory =
      new CurrentTimeIncrementalNonceFactory(TimeUnit.MILLISECONDS);
  protected final String apiKey;

  @SneakyThrows
  public FineryMarketsBaseService(Exchange exchange) {
    fineryMarketsAuthenticated =
        ExchangeRestProxyBuilder.forInterface(
                FineryMarketsAuthenticated.class, exchange.getExchangeSpecification())
            //            .clientConfigCustomizer(
            //                clientConfig ->
            //                    clientConfig.setJacksonObjectMapperFactory(
            //                        new FineryMarketsJacksonObjectMapperFactory()))
            .restProxyFactory(
                ProxyConfig.getInstance()
                    .getRestProxyFactoryClass()
                    .getDeclaredConstructor()
                    .newInstance())
            .build();
    signatureCreator =
        FineryMarketsDigest.createInstance(exchange.getExchangeSpecification().getSecretKey());
    apiKey = exchange.getExchangeSpecification().getApiKey();
  }
}
