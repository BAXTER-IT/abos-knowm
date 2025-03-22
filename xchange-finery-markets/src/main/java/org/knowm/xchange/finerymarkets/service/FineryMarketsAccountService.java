package org.knowm.xchange.finerymarkets.service;

import static org.knowm.xchange.dto.ResponseStatus.ERROR;
import static org.knowm.xchange.dto.ResponseStatus.OK;

import org.knowm.xchange.dto.ExchangeResponse;
import org.knowm.xchange.finerymarkets.FineryMarketsException;
import org.knowm.xchange.finerymarkets.FineryMarketsExchange;
import org.knowm.xchange.finerymarkets.dto.DecoratedPayload;
import org.knowm.xchange.dto.account.ToggleTradingRequest;
import org.knowm.xchange.service.account.AccountService;

public class FineryMarketsAccountService extends FineryMarketsAccountServiceRaw
    implements AccountService {

  public FineryMarketsAccountService(FineryMarketsExchange exchange) {
    super(exchange);
  }

  public ExchangeResponse enableTrading(ToggleTradingRequest req) {
    DecoratedPayload payload = new DecoratedPayload(req);
    try {
      fineryMarketsAuthenticated.enableTrading(apiKey, signatureCreator, payload);
    } catch (FineryMarketsException e) {
      return new ExchangeResponse(ERROR);
    }
    return new ExchangeResponse(OK);
  }

  public ExchangeResponse disableTrading(ToggleTradingRequest req) {
    DecoratedPayload payload = new DecoratedPayload(req);
    try {
      fineryMarketsAuthenticated.disableTrading(apiKey, signatureCreator, payload);
    } catch (FineryMarketsException e) {
      return new ExchangeResponse(ERROR);
    }
    return new ExchangeResponse(OK);
  }
}
