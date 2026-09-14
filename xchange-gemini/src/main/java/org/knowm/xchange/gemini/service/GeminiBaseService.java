package org.knowm.xchange.gemini.service;

import org.knowm.xchange.client.ExchangeRestProxyBuilder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.knowm.xchange.gemini.GeminiAuthenticatedV1;
import org.knowm.xchange.gemini.GeminiExchange;
import org.knowm.xchange.gemini.GeminiV1;
import org.knowm.xchange.gemini.GeminiV2;
import org.knowm.xchange.gemini.exceptions.GeminiException;
import org.knowm.xchange.service.BaseExchangeService;
import org.knowm.xchange.service.BaseService;
import si.mazi.rescu.ParamsDigest;

public class GeminiBaseService extends BaseExchangeService<GeminiExchange> implements BaseService {

  protected final String apiKey;
  protected final GeminiV1 geminiV1;
  protected final GeminiV2 geminiV2;
  protected final GeminiAuthenticatedV1 geminiAuthenticatedV1;
  protected final ParamsDigest signatureCreator;
  protected final ParamsDigest payloadCreator;

  /**
   * Constructor
   *
   * @param exchange
   */
  public GeminiBaseService(GeminiExchange exchange) {
    super(exchange);
    geminiV1 =
        ExchangeRestProxyBuilder.forInterface(GeminiV1.class, exchange.getExchangeSpecification())
            .build();
    geminiV2 =
        ExchangeRestProxyBuilder.forInterface(GeminiV2.class, exchange.getExchangeSpecification())
            .build();
    geminiAuthenticatedV1 =
        ExchangeRestProxyBuilder.forInterface(
                GeminiAuthenticatedV1.class, exchange.getExchangeSpecification())
            .build();

    apiKey = exchange.getExchangeSpecification().getApiKey();
    signatureCreator =
        GeminiHmacPostBodyDigest.createInstance(exchange.getExchangeSpecification().getSecretKey());
    payloadCreator = new GeminiPayloadDigest();
  }

  protected ExchangeException handleException(GeminiException e) {
    if (e.getMessage().contains("due to insufficient funds")
        || e.getMessage().contains("you do not have enough available")) {
      return new FundsExceededException(e);
    }

    return new ExchangeException(e);
  }
}
