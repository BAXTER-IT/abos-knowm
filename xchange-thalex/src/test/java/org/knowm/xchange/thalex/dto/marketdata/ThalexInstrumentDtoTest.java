package org.knowm.xchange.thalex.dto.marketdata;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import org.junit.Test;
import org.knowm.xchange.derivative.OptionsContract.OptionType;
import org.knowm.xchange.thalex.dto.enums.ThalexInstrumentType;

public class ThalexInstrumentDtoTest {
  @Test
  public void testThalexInstrumentDto_equalsAndToString() {
    ThalexInstrumentLegDto leg = ThalexInstrumentLegDto.builder()
        .instrumentName("ETH-26MAY25")
        .quantity(1)
        .build();

    ThalexInstrumentDto dto1 = ThalexInstrumentDto.builder()
        .instrumentName("ETH-26MAY25-PERPETUAL")
        .product("BTC-USD")
        .tickSize(new BigDecimal("0.1"))
        .volumeTickSize(new BigDecimal("0.01"))
        .minOrderAmount(new BigDecimal("0.01"))
        .underlying("BTCUSD")
        .type(ThalexInstrumentType.OPTION)
        .optionType(OptionType.CALL)
        .expiryDate("2025-01-01")
        .expirationTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
        .strikePrice(new BigDecimal("30000"))
        .baseCurrency("USD")
        .legs(Collections.singletonList(leg))
        .createTime(Instant.parse("2024-01-01T00:00:00Z"))
        .settlementPrice(new BigDecimal("31000"))
        .settlementIndexPrice(new BigDecimal("30900"))
        .build();

    ThalexInstrumentDto dto2 = ThalexInstrumentDto.builder()
        .instrumentName("ETH-26MAY25-PERPETUAL")
        .product("BTC-USD")
        .tickSize(new BigDecimal("0.1"))
        .volumeTickSize(new BigDecimal("0.01"))
        .minOrderAmount(new BigDecimal("0.01"))
        .underlying("BTCUSD")
        .type(ThalexInstrumentType.OPTION)
        .optionType(OptionType.CALL)
        .expiryDate("2025-01-01")
        .expirationTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
        .strikePrice(new BigDecimal("30000"))
        .baseCurrency("USD")
        .legs(Collections.singletonList(leg))
        .createTime(Instant.parse("2024-01-01T00:00:00Z"))
        .settlementPrice(new BigDecimal("31000"))
        .settlementIndexPrice(new BigDecimal("30900"))
        .build();

    assertEquals(dto1, dto2);
    assertEquals(dto1.hashCode(), dto2.hashCode());

    String expectedToString =
        "ThalexInstrumentDto(instrumentName=ETH-26MAY25-PERPETUAL, product=BTC-USD, tickSize=0.1, volumeTickSize=0.01, " +
            "minOrderAmount=0.01, underlying=BTCUSD, type=OPTION, optionType=CALL, expiryDate=2025-01-01, " +
            "expirationTimestamp=2025-01-01T00:00:00Z, strikePrice=30000, baseCurrency=USD, legs=[" +
            "ThalexInstrumentLegDto(instrumentName=ETH-26MAY25, quantity=1)], " +
            "createTime=2024-01-01T00:00:00Z, settlementPrice=31000, settlementIndexPrice=30900)";

    assertEquals(expectedToString, dto1.toString());
  }


}