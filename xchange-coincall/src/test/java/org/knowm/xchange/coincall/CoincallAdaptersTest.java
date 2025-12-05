package org.knowm.xchange.coincall;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.Test;
import org.knowm.xchange.coincall.CoincallAdapters.ParsedOptionSymbol;
import org.knowm.xchange.derivative.OptionsContract.OptionType;

public class CoincallAdaptersTest {

  @Test
  public void testParseOptionSymbol_WhenDateIs2JUN23() {
    ParsedOptionSymbol p =
        CoincallAdapters.parseOptionSymbol("BTCUSD-2JUN23-18000-C");

    assertEquals("BTC", p.base);
    assertEquals(new BigDecimal("18000"), p.strike);
    assertEquals(OptionType.CALL, p.type);

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    cal.setTime(p.expiry);
    assertEquals(2023, cal.get(Calendar.YEAR));
    assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
    assertEquals(2, cal.get(Calendar.DAY_OF_MONTH));
  }

  @Test
  public void testParseOptionSymbol_WhenDateIs22JUN23() {
    ParsedOptionSymbol p =
        CoincallAdapters.parseOptionSymbol("BTCUSD-22JUN23-18000-C");

    assertEquals("BTC", p.base);
    assertEquals(new BigDecimal("18000"), p.strike);
    assertEquals(OptionType.CALL, p.type);

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    cal.setTime(p.expiry);
    assertEquals(2023, cal.get(Calendar.YEAR));
    assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
    assertEquals(22, cal.get(Calendar.DAY_OF_MONTH));
  }
}