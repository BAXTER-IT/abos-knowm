package org.knowm.xchange.coincall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallFuturesInstrumentDto;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallLotSizeFilter;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallPriceFilter;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallSpotInstrumentDto;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.derivative.FuturesContract;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.instrument.Instrument;

public class CoincallAdaptersInstrumentTest {

  @Test
  public void testBuildFuturesMeta_usesLastPriceScale_andBaseVolume() {
    CoincallFuturesInstrumentDto dto = CoincallFuturesInstrumentDto.builder()
        .baseCurrency("BTC")
        .quoteCurrency("USD")
        .productType("perp")
        .lastPrice(new BigDecimal("42000.123"))
        .baseVolume(new BigDecimal("5.0000"))
        .build();

    InstrumentMetaData meta = CoincallAdapters.buildFuturesMeta(dto);

    // priceScale from lastPrice.scale()
    assertEquals(Integer.valueOf(3), meta.getPriceScale());
    assertEquals(new BigDecimal("0.001"), meta.getPriceStepSize());

    // volumeScale should be scale(baseVolume)
    assertEquals(Integer.valueOf(4), meta.getVolumeScale());

    // fee currency always quote currency
    assertEquals(Currency.getInstance("USD"), meta.getTradingFeeCurrency());

    // futures always allow market orders
    assertTrue(meta.isMarketOrderEnabled());
  }

  @Test
  public void testBuildFuturesMeta_usesIndexPrice_ifLastPriceNull() {
    CoincallFuturesInstrumentDto dto = CoincallFuturesInstrumentDto.builder()
        .baseCurrency("ETH")
        .quoteCurrency("USDT")
        .productType("quarter")
        .lastPrice(null)
        .indexPrice(new BigDecimal("2500.55"))
        .baseVolume(new BigDecimal("1.2"))
        .build();

    InstrumentMetaData meta = CoincallAdapters.buildFuturesMeta(dto);

    assertEquals(Integer.valueOf(2), meta.getPriceScale());
    assertEquals(new BigDecimal("0.01"), meta.getPriceStepSize());
    assertEquals(Integer.valueOf(1), meta.getVolumeScale());
    assertEquals(Currency.getInstance("USDT"), meta.getTradingFeeCurrency());
    assertTrue(meta.isMarketOrderEnabled());
  }

  @Test
  public void testBuildFuturesMeta_usesContractPrice_ifOthersNull() {
    CoincallFuturesInstrumentDto dto = CoincallFuturesInstrumentDto.builder()
        .baseCurrency("SOL")
        .quoteCurrency("USD")
        .productType("perp")
        .contractPrice(new BigDecimal("100.00001"))
        .baseVolume(new BigDecimal("0.5"))
        .build();

    InstrumentMetaData meta = CoincallAdapters.buildFuturesMeta(dto);

    assertEquals(Integer.valueOf(5), meta.getPriceScale());
    assertEquals(new BigDecimal("0.00001"), meta.getPriceStepSize());
    assertEquals(Integer.valueOf(1), meta.getVolumeScale());
    assertEquals(Currency.getInstance("USD"), meta.getTradingFeeCurrency());
  }

  @Test
  public void testToInstrumentsMapEntry_basic() {
    CoincallFuturesInstrumentDto dto = CoincallFuturesInstrumentDto.builder()
        .baseCurrency("BTC")
        .quoteCurrency("USD")
        .productType("perp")
        .lastPrice(new BigDecimal("30000.12"))
        .baseVolume(new BigDecimal("10"))
        .build();

    SimpleEntry<Instrument, InstrumentMetaData> entry =
        CoincallAdapters.toInstrumentsMapEntry(dto);

    // FUTURES CONTRACT
    FuturesContract contract = (FuturesContract) entry.getKey();
    assertEquals(new CurrencyPair("BTC", "USD"), contract.getCurrencyPair());
    assertEquals("PERP", contract.getPrompt()); // uppercased productType

    // METADATA (already tested above, but verify linkage)
    InstrumentMetaData meta = entry.getValue();
    assertEquals(Integer.valueOf(2), meta.getPriceScale());
    assertEquals(new BigDecimal("0.01"), meta.getPriceStepSize());
    assertEquals(Integer.valueOf(0), meta.getVolumeScale());
    assertEquals(Currency.getInstance("USD"), meta.getTradingFeeCurrency());
    assertTrue(meta.isMarketOrderEnabled());
  }

  @Test
  public void testToInstrumentsMapEntrySpot_basic() {
    CoincallLotSizeFilter lot = CoincallLotSizeFilter.builder()
        .minQuantity(new BigDecimal("0.0001"))
        .maxQuantity(new BigDecimal("5"))
        .maxOrderSize(new BigDecimal("150000"))
        .basePrecision(3)
        .quotePrecision(2)
        .build();

    CoincallPriceFilter price = CoincallPriceFilter.builder()
        .tickSize(new BigDecimal("0.01"))
        .build();

    CoincallSpotInstrumentDto dto = CoincallSpotInstrumentDto.builder()
        .baseCoin("BTC")
        .quoteCoin("USDT")
        .lotSizeFilter(lot)
        .priceFilter(price)
        .enableTrading(true)
        .build();

    SimpleEntry<Instrument, InstrumentMetaData> entry =
        CoincallAdapters.toInstrumentsMapEntry(dto);

    // ---------- Instrument ----------
    CurrencyPair pair = (CurrencyPair) entry.getKey();
    assertEquals(new CurrencyPair("BTC", "USDT"), pair);

    // ---------- MetaData ----------
    InstrumentMetaData meta = entry.getValue();

    // Quantity constraints
    assertEquals(new BigDecimal("0.0001"), meta.getMinimumAmount());
    assertEquals(new BigDecimal("5"), meta.getMaximumAmount());
    assertEquals(0, new BigDecimal("150000").compareTo(meta.getCounterMaximumAmount()));

    // Precision
    assertEquals(Integer.valueOf(3), meta.getVolumeScale()); // base precision
    assertEquals(Integer.valueOf(2), meta.getPriceScale());  // quote precision

    // Step sizes
    assertEquals(new BigDecimal("0.001"), meta.getAmountStepSize()); // 10^-3
    assertEquals(new BigDecimal("0.01"), meta.getPriceStepSize());

    // Fee currency = quote coin
    assertEquals(Currency.getInstance("USDT"), meta.getTradingFeeCurrency());

    // Market order support
    assertTrue(meta.isMarketOrderEnabled());
  }

  @Test
  public void testToInstrumentsMapEntrySpot_noPriceFilter() {
    CoincallLotSizeFilter lot = CoincallLotSizeFilter.builder()
        .minQuantity(new BigDecimal("1"))
        .maxQuantity(new BigDecimal("100"))
        .basePrecision(0)
        .quotePrecision(4)
        .build();

    CoincallSpotInstrumentDto dto = CoincallSpotInstrumentDto.builder()
        .baseCoin("ETH")
        .quoteCoin("USD")
        .lotSizeFilter(lot)
        .priceFilter(null)
        .enableTrading(false)
        .build();

    SimpleEntry<Instrument, InstrumentMetaData> entry =
        CoincallAdapters.toInstrumentsMapEntry(dto);

    CurrencyPair pair = (CurrencyPair) entry.getKey();
    assertEquals(new CurrencyPair("ETH", "USD"), pair);

    InstrumentMetaData meta = entry.getValue();

    assertEquals(new BigDecimal("1"), meta.getMinimumAmount());
    assertEquals(0, new BigDecimal("100").compareTo(meta.getMaximumAmount()));
    assertNull(meta.getPriceStepSize()); // no price filter tickSize

    // Precision
    assertEquals(Integer.valueOf(0), meta.getVolumeScale());
    assertEquals(Integer.valueOf(4), meta.getPriceScale());

    assertEquals(new BigDecimal("1"), meta.getAmountStepSize()); // 10^0

    assertEquals(Currency.getInstance("USD"), meta.getTradingFeeCurrency());
    assertFalse(meta.isMarketOrderEnabled()); // disabled
  }

  @Test
  public void testToInstrumentsMap_spot_filtersDisabledAndMapsEnabled() {
    // --- Enabled instrument ---
    CoincallSpotInstrumentDto enabled = CoincallSpotInstrumentDto.builder()
        .baseCoin("BTC")
        .quoteCoin("USDT")
        .enableTrading(true)
        .lotSizeFilter(CoincallLotSizeFilter.builder()
            .basePrecision(3)
            .quotePrecision(2)
            .minQuantity(new BigDecimal("0.001"))
            .maxQuantity(new BigDecimal("5"))
            .build())
        .build();

    // --- Disabled instrument (should be filtered) ---
    CoincallSpotInstrumentDto disabled = CoincallSpotInstrumentDto.builder()
        .baseCoin("ETH")
        .quoteCoin("USDT")
        .enableTrading(false)
        .lotSizeFilter(CoincallLotSizeFilter.builder()
            .basePrecision(3)
            .quotePrecision(2)
            .minQuantity(new BigDecimal("0.01"))
            .maxQuantity(new BigDecimal("10"))
            .build())
        .build();

    List<CoincallSpotInstrumentDto> list = Arrays.asList(enabled, disabled);

    Map<Instrument, InstrumentMetaData> map = CoincallAdapters.toInstrumentsMap(list);

    // Should contain only the enabled BTC/USDT pair
    assertEquals(1, map.size());
    assertTrue(map.containsKey(new CurrencyPair("BTC", "USDT")));
  }

  @Test
  public void testToInstumentsMap_futures_mapsAllEntries() {
    CoincallFuturesInstrumentDto dto1 = CoincallFuturesInstrumentDto.builder()
        .baseCurrency("BTC")
        .quoteCurrency("USD")
        .productType("perp")
        .lastPrice(new BigDecimal("30000.12"))
        .baseVolume(new BigDecimal("10"))
        .build();

    CoincallFuturesInstrumentDto dto2 = CoincallFuturesInstrumentDto.builder()
        .baseCurrency("ETH")
        .quoteCurrency("USD")
        .productType("quarter")
        .indexPrice(new BigDecimal("2500.55"))
        .baseVolume(new BigDecimal("5"))
        .build();

    Map<Instrument, InstrumentMetaData> map =
        CoincallAdapters.toInstumentsMap(Arrays.asList(dto1, dto2));

    assertEquals(2, map.size());

    assertTrue(map.containsKey(
        new FuturesContract(new CurrencyPair("BTC", "USD"), "PERP")));
    assertTrue(map.containsKey(
        new FuturesContract(new CurrencyPair("ETH", "USD"), "QUARTER")));

    // Spot-check metadata was generated:
    InstrumentMetaData btcMeta = map.get(
        new FuturesContract(new CurrencyPair("BTC", "USD"), "PERP"));
    assertNotNull(btcMeta);
    assertEquals(Integer.valueOf(2), btcMeta.getPriceScale()); // from 30000.12
  }
}
