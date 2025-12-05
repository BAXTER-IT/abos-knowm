package org.knowm.xchange.coincall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.knowm.xchange.coincall.dtos.account.CoincallFuturesPositionDto;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.derivative.FuturesContract;
import org.knowm.xchange.dto.account.OpenPosition;

public class CoincallAdaptersOpenPositionTest {

  @Test
  public void testToOpenPosition_long() {
    CoincallFuturesPositionDto dto = new CoincallFuturesPositionDto();
    dto.setDisplayName("BTC-PERP");
    dto.setSymbol("BTCUSD");
    dto.setQty(new BigDecimal("5"));            // positive → LONG
    dto.setAvgPrice(new BigDecimal("40000"));
    dto.setUpnl(new BigDecimal("250"));

    OpenPosition p = CoincallAdapters.toOpenPosition(dto);

    assertNotNull(p);

    // instrument from toFuturesContract()
    FuturesContract fc = (FuturesContract) p.getInstrument();
    assertEquals(new CurrencyPair("BTC", "USD"), fc.getCurrencyPair());
    assertEquals("PERP", fc.getPrompt());

    // direct field mappings
    assertEquals(new BigDecimal("40000"), p.getPrice());
    assertEquals(new BigDecimal("5"), p.getSize());
    assertEquals(OpenPosition.Type.LONG, p.getType());
    assertEquals(new BigDecimal("250"), p.getUnRealisedPnl());
  }

  @Test
  public void testToOpenPosition_short() {
    CoincallFuturesPositionDto dto = new CoincallFuturesPositionDto();
    dto.setDisplayName("ETH-QUARTER");
    dto.setSymbol("ETHUSD");
    dto.setQty(new BigDecimal("-2"));          // negative → SHORT
    dto.setAvgPrice(new BigDecimal("2500"));
    dto.setUpnl(new BigDecimal("-100"));

    OpenPosition p = CoincallAdapters.toOpenPosition(dto);

    assertNotNull(p);

    FuturesContract fc = (FuturesContract) p.getInstrument();
    assertEquals(new CurrencyPair("ETH", "USD"), fc.getCurrencyPair());
    assertEquals("QUARTER", fc.getPrompt());

    assertEquals(new BigDecimal("2500"), p.getPrice());
    assertEquals(new BigDecimal("-2"), p.getSize());
    assertEquals(OpenPosition.Type.SHORT, p.getType());
    assertEquals(new BigDecimal("-100"), p.getUnRealisedPnl());
  }

  @Test
  public void testToOpenPositions_mapsAll() {
    CoincallFuturesPositionDto dto1 = new CoincallFuturesPositionDto();
    dto1.setDisplayName("BTC-PERP");
    dto1.setSymbol("BTCUSD");
    dto1.setQty(new BigDecimal("1"));
    dto1.setAvgPrice(new BigDecimal("41000"));
    dto1.setUpnl(new BigDecimal("50"));

    CoincallFuturesPositionDto dto2 = new CoincallFuturesPositionDto();
    dto2.setDisplayName("ETH-PERP");
    dto2.setSymbol("ETHUSD");
    dto2.setQty(new BigDecimal("-3"));
    dto2.setAvgPrice(new BigDecimal("2300"));
    dto2.setUpnl(new BigDecimal("-120"));

    List<OpenPosition> result = CoincallAdapters.toOpenPositions(Arrays.asList(dto1, dto2));

    assertEquals(2, result.size());

    // basic sanity checks
    assertEquals(OpenPosition.Type.LONG, result.get(0).getType());
    assertEquals(OpenPosition.Type.SHORT, result.get(1).getType());
  }
}
