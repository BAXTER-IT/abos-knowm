package org.knowm.xchange.finerymarkets.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.finerymarkets.dto.DecoratedPayload;
import org.knowm.xchange.finerymarkets.dto.trade.request.DealHistoryRequest;
import org.knowm.xchange.finerymarkets.dto.trade.response.DealHistoryResponse;

public class FineryMarketsTradeServiceRaw extends FineryMarketsBaseService {

  public FineryMarketsTradeServiceRaw(Exchange exchange) {
    super(exchange);
  }

  protected DealHistoryResponse getDealHistory(DealHistoryRequest req) {
    DecoratedPayload payload = new DecoratedPayload(req);
    return fineryMarketsAuthenticated.getDealHistory(apiKey, signatureCreator, payload);
  }
}
