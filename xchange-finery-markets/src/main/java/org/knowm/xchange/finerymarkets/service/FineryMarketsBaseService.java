package org.knowm.xchange.finerymarkets.service;

import lombok.SneakyThrows;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.client.ExchangeRestProxyBuilder;
import org.knowm.xchange.finerymarkets.FineryMarketsAuthenticated;
import org.knowm.xchange.finerymarkets.utils.FineryMarketsDigestInterceptor;
import org.knowm.xchange.service.BaseService;
import si.mazi.rescu.ParamsDigest;

public class FineryMarketsBaseService implements BaseService {

  protected final FineryMarketsAuthenticated fineryMarketsAuthenticated;
  protected final ParamsDigest signatureCreator;
  protected final String apiKey;

  @SneakyThrows
  public FineryMarketsBaseService(Exchange exchange) {
    fineryMarketsAuthenticated =
        ExchangeRestProxyBuilder.forInterface(
                FineryMarketsAuthenticated.class, exchange.getExchangeSpecification())
            .customInterceptor(new FineryMarketsDigestInterceptor())
            .build();
    signatureCreator =
        FineryMarketsDigest.createInstance(exchange.getExchangeSpecification().getSecretKey());
    apiKey = exchange.getExchangeSpecification().getApiKey();
  }
}
