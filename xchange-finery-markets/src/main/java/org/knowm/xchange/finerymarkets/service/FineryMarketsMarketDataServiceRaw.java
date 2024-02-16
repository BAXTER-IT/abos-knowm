package org.knowm.xchange.finerymarkets.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.finerymarkets.dto.FineryMarketsDecoratedPayload;
import org.knowm.xchange.finerymarkets.dto.marketdata.response.FineryMarketsInstrumentsResponse;

public class FineryMarketsMarketDataServiceRaw extends FineryMarketsBaseService {

  public FineryMarketsMarketDataServiceRaw(Exchange exchange) {
    super(exchange);
  }

  FineryMarketsInstrumentsResponse getFineryMarketsInstruments() {
    FineryMarketsDecoratedPayload payload = new FineryMarketsDecoratedPayload();
    return fineryMarketsAuthenticated.getInstruments(apiKey, signatureCreator, payload);
  }
}
