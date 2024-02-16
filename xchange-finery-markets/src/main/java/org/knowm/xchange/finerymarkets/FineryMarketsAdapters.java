package org.knowm.xchange.finerymarkets;

import java.util.Map;
import java.util.stream.Collectors;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsCurrency;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsInstrument;
import org.knowm.xchange.finerymarkets.dto.marketdata.response.FineryMarketsInstrumentsResponse;
import org.knowm.xchange.instrument.Instrument;

public class FineryMarketsAdapters {

  private FineryMarketsAdapters() {
  }

  public static Map<Currency, CurrencyMetaData> adaptCurrencies(
      FineryMarketsInstrumentsResponse input) {
    return input.getCurrencies().stream()
        .distinct()
        .collect(
            Collectors.toMap(
                FineryMarketsAdapters::adaptCurrency,
                FineryMarketsAdapters::adaptCurrencyMetaData));
  }

  public static Map<Instrument, InstrumentMetaData> adaptInstruments(
      FineryMarketsInstrumentsResponse input) {
    return input.getInstruments().stream()
        .distinct()
        .collect(
            Collectors.toMap(
                FineryMarketsAdapters::adaptInstrument,
                FineryMarketsAdapters::adaptInstrumentMetaData));
  }

  private static CurrencyPair adaptInstrument(FineryMarketsInstrument input) {
    return new CurrencyPair(input.getAssetCurrencyName(), input.getBalanceCurrencyName());
  }

  private static InstrumentMetaData adaptInstrumentMetaData(FineryMarketsInstrument input) {
    return new InstrumentMetaData.Builder().build();
  }

  private static Currency adaptCurrency(FineryMarketsCurrency input) {
    return new Currency(input.getName());
  }

  private static CurrencyMetaData adaptCurrencyMetaData(FineryMarketsCurrency input) {
    return CurrencyMetaData.builder().scale(input.getBalanceSize()).build();
  }
}
