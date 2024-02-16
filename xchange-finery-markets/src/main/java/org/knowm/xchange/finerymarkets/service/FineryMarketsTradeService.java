package org.knowm.xchange.finerymarkets.service;

import org.knowm.xchange.finerymarkets.FineryMarketsExchange;
import org.knowm.xchange.service.trade.TradeService;

public class FineryMarketsTradeService extends FineryMarketsTradeServiceRaw
    implements TradeService {

  public FineryMarketsTradeService(FineryMarketsExchange exchange) {
    super(exchange);
  }
}
