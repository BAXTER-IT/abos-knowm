package org.knowm.xchange.finerymarkets.service;

import static org.knowm.xchange.finerymarkets.FineryMarketsAdapters.adaptCurrencies;
import static org.knowm.xchange.finerymarkets.FineryMarketsAdapters.adaptInstruments;

import java.io.IOException;
import java.util.Map;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.finerymarkets.FineryMarketsAdapters;
import org.knowm.xchange.finerymarkets.FineryMarketsExchange;
import org.knowm.xchange.finerymarkets.dto.marketdata.response.FineryMarketsInstrumentsResponse;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class FineryMarketsMarketDataService extends FineryMarketsMarketDataServiceRaw
    implements MarketDataService {
  public FineryMarketsMarketDataService(FineryMarketsExchange exchange) {
    super(exchange);
  }

  @Override
  public Map<Currency, CurrencyMetaData> getCurrencies() {
    return adaptCurrencies(getFineryMarketsInstruments());
  }

  @Override
  public Map<Instrument, InstrumentMetaData> getInstruments() {
    return adaptInstruments(getFineryMarketsInstruments());
  }
}
