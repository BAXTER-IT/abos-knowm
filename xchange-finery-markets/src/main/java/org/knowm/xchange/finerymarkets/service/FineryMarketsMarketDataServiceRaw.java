package org.knowm.xchange.finerymarkets.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.finerymarkets.dto.DecoratedPayload;
import org.knowm.xchange.finerymarkets.dto.marketdata.response.InstrumentsResponse;

public class FineryMarketsMarketDataServiceRaw extends FineryMarketsBaseService {

  public FineryMarketsMarketDataServiceRaw(Exchange exchange) {
    super(exchange);
  }

  InstrumentsResponse getFineryMarketsInstruments() {
    DecoratedPayload payload = new DecoratedPayload();
    return fineryMarketsAuthenticated.getInstruments(apiKey, signatureCreator, payload);
  }
}
