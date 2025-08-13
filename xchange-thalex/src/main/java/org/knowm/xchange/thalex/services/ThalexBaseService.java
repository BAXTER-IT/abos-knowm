package org.knowm.xchange.thalex.services;

import org.knowm.xchange.client.ExchangeRestProxyBuilder;
import org.knowm.xchange.service.BaseExchangeService;
import org.knowm.xchange.service.BaseService;
import org.knowm.xchange.thalex.Thalex;
import org.knowm.xchange.thalex.ThalexAuthenticated;
import org.knowm.xchange.thalex.ThalexExchange;
import org.knowm.xchange.thalex.config.ThalexJacksonObjectMapperFactory;
import si.mazi.rescu.ThalexRestProxyFactory;

public class ThalexBaseService extends BaseExchangeService<ThalexExchange> implements BaseService {

  protected final String apiKey;
  protected final Thalex thalex;
  protected final ThalexAuthenticated thalexAuthenticated;
  protected final ThalexDigest thalexDigest;

  public ThalexBaseService(ThalexExchange exchange) {
    super(exchange);
    thalex =
        ExchangeRestProxyBuilder.forInterface(Thalex.class, exchange.getExchangeSpecification())
            .clientConfigCustomizer(
                clientConfig ->
                    clientConfig.setJacksonObjectMapperFactory(
                        new ThalexJacksonObjectMapperFactory()))
            .restProxyFactory(new ThalexRestProxyFactory())
            .build();
    thalexAuthenticated =
        ExchangeRestProxyBuilder.forInterface(
                ThalexAuthenticated.class, exchange.getExchangeSpecification())
            .clientConfigCustomizer(
                clientConfig ->
                    clientConfig.setJacksonObjectMapperFactory(
                        new ThalexJacksonObjectMapperFactory()))
            .restProxyFactory(new ThalexRestProxyFactory())
            .build();

    apiKey = exchange.getExchangeSpecification().getApiKey();
    String apiSecret = exchange.getExchangeSpecification().getSecretKey();
    thalexDigest = new ThalexDigest(apiKey, apiSecret);
  }
}
