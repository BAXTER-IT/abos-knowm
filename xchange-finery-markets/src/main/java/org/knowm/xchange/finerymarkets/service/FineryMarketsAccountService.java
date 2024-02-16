package org.knowm.xchange.finerymarkets.service;

import org.knowm.xchange.finerymarkets.FineryMarketsExchange;
import org.knowm.xchange.service.account.AccountService;

public class FineryMarketsAccountService extends FineryMarketsAccountServiceRaw
    implements AccountService {

  public FineryMarketsAccountService(FineryMarketsExchange exchange) {
    super(exchange);
  }
}
