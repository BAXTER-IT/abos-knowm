package org.knowm.xchange.coincall.services;

import org.knowm.xchange.client.ExchangeRestProxyBuilder;
import org.knowm.xchange.coincall.Coincall;
import org.knowm.xchange.coincall.CoincallAuthenticated;
import org.knowm.xchange.coincall.CoincallExchange;
import org.knowm.xchange.coincall.configs.CoincallJacksonObjectMapperFactory;
import org.knowm.xchange.service.BaseExchangeService;
import org.knowm.xchange.service.BaseService;

public class CoincallBaseService extends BaseExchangeService<CoincallExchange> implements
    BaseService {

  protected final String apiKey;
  protected final Coincall coincall;
  protected final CoincallAuthenticated coincallAuthenticated;
  protected final CoincallDigest coincallDigest;

  public CoincallBaseService(CoincallExchange exchange) {
    super(exchange);

    coincall =
        ExchangeRestProxyBuilder.forInterface(Coincall.class, exchange.getExchangeSpecification())
            .clientConfigCustomizer(
                clientConfig ->
                    clientConfig.setJacksonObjectMapperFactory(
                        new CoincallJacksonObjectMapperFactory()))
            .build();

    coincallAuthenticated =
        ExchangeRestProxyBuilder.forInterface(
                CoincallAuthenticated.class, exchange.getExchangeSpecification())
            .clientConfigCustomizer(
                clientConfig ->
                    clientConfig.setJacksonObjectMapperFactory(
                        new CoincallJacksonObjectMapperFactory()))
            .build();

    apiKey = exchange.getExchangeSpecification().getApiKey();

    String apiSecret = exchange.getExchangeSpecification().getSecretKey();

    coincallDigest = new CoincallDigest(apiKey, apiSecret);
  }
}
