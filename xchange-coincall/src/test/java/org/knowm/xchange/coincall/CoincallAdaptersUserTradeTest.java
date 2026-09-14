package org.knowm.xchange.coincall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.junit.Test;
import org.knowm.xchange.coincall.CoincallAdapters.ParsedOptionSymbol;
import org.knowm.xchange.coincall.dtos.enums.CoincallTradeSide;
import org.knowm.xchange.coincall.dtos.trade.CoincallFuturesTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallOptionTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallSpotFillDto;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.derivative.FuturesContract;
import org.knowm.xchange.derivative.OptionsContract;
import org.knowm.xchange.derivative.OptionsContract.OptionType;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.enums.MarketParticipant;

public class CoincallAdaptersUserTradeTest {

  @Test
  public void testToCurrencyPair_basic() {
    CurrencyPair pair = CoincallAdapters.toCurrencyPair("BTC/USDT");

    assertEquals("BTC", pair.base.getCurrencyCode());
    assertEquals("USDT", pair.counter.getCurrencyCode());
  }

  @Test
  public void testToFuturesContract_basic() {
    String displayName = "BTC-PERP";
    String symbol = "BTCUSD";

    FuturesContract fc = CoincallAdapters.toFuturesContract(displayName, symbol);

    assertEquals(new CurrencyPair("BTC", "USD"), fc.getCurrencyPair());
    assertEquals("PERP", fc.getPrompt());
  }

  @Test
  public void testToFuturesContract_ethExample() {
    String displayName = "ETH-QUARTER";
    String symbol = "ETHUSD";

    FuturesContract fc = CoincallAdapters.toFuturesContract(displayName, symbol);

    assertEquals(new CurrencyPair("ETH", "USD"), fc.getCurrencyPair());
    assertEquals("QUARTER", fc.getPrompt());
  }

  @Test
  public void testParseExpiry_basic() {
    Date result = CoincallAdapters.parseExpiry("3MAR24");

    LocalDate expected = LocalDate.of(2024, 3, 3);
    Date expectedDate = Date.from(expected.atStartOfDay(ZoneId.of("UTC")).toInstant());

    assertEquals(expectedDate, result);
  }

  @Test
  public void testParseExpiry_caseInsensitive() {
    Date result = CoincallAdapters.parseExpiry("15dec25");

    LocalDate expected = LocalDate.of(2025, 12, 15);
    Date expectedDate = Date.from(expected.atStartOfDay(ZoneId.of("UTC")).toInstant());

    assertEquals(expectedDate, result);
  }

  @Test
  public void testParseOptionType_callUppercase() {
    assertEquals(OptionType.CALL, CoincallAdapters.parseOptionType("C"));
  }

  @Test
  public void testParseOptionType_putLowercase() {
    assertEquals(OptionType.PUT, CoincallAdapters.parseOptionType("p"));
  }

  @Test
  public void testParseOptionSymbol_basic() {
    ParsedOptionSymbol out =
        CoincallAdapters.parseOptionSymbol("BTC-3MAR24-50000-C");

    assertEquals("BTC", out.base);
    assertEquals(new BigDecimal("50000"), out.strike);
    assertEquals(OptionType.CALL, out.type);

    LocalDate expected = LocalDate.of(2024, 3, 3);
    Date expectedDate = Date.from(expected.atStartOfDay(ZoneId.of("UTC")).toInstant());
    assertEquals(expectedDate, out.expiry);
  }

  @Test
  public void testParseOptionSymbol_baseEndingWithUSD() {
    // Should strip the trailing "USD" → base becomes "ETH"
    ParsedOptionSymbol out =
        CoincallAdapters.parseOptionSymbol("ETHUSD-15DEC25-2000-P");

    assertEquals("ETH", out.base);
    assertEquals(new BigDecimal("2000"), out.strike);
    assertEquals(OptionType.PUT, out.type);

    LocalDate expected = LocalDate.of(2025, 12, 15);
    Date expectedDate = Date.from(expected.atStartOfDay(ZoneId.of("UTC")).toInstant());
    assertEquals(expectedDate, out.expiry);
  }

  @Test
  public void testToOptionsContract_basic() {
    OptionsContract c =
        CoincallAdapters.toOptionsContract("BTC-3MAR24-50000-C");

    // currencyPair = (base, USD)
    assertEquals(new CurrencyPair("BTC", "USD"), c.getCurrencyPair());

    // expiry
    LocalDate expectedDate = LocalDate.of(2024, 3, 3);
    Date expected = Date.from(expectedDate.atStartOfDay(ZoneId.of("UTC")).toInstant());
    assertEquals(expected, c.getExpireDate());

    // strike
    assertEquals(new BigDecimal("50000"), c.getStrike());

    // option type
    assertEquals(OptionType.CALL, c.getType());
  }

  @Test
  public void testToOptionsContract_stripUsdSuffix() {
    // ETHUSD should become base = "ETH"
    OptionsContract c =
        CoincallAdapters.toOptionsContract("ETHUSD-15DEC25-2000-P");

    assertEquals(new CurrencyPair("ETH", "USD"), c.getCurrencyPair());

    LocalDate expected = LocalDate.of(2025, 12, 15);
    Date expectedDate = Date.from(expected.atStartOfDay(ZoneId.of("UTC")).toInstant());
    assertEquals(expectedDate, c.getExpireDate());

    assertEquals(new BigDecimal("2000"), c.getStrike());
    assertEquals(OptionType.PUT, c.getType());
  }

  @Test
  public void testToUserTradeFutures_buyTaker() {
    CoincallFuturesTransactionDetail dto = CoincallFuturesTransactionDetail.builder()
        .tradeSide(CoincallTradeSide.BUY)
        .displayName("BTC-PERP")
        .symbol("BTCUSD")
        .qty(new BigDecimal("3"))
        .price(new BigDecimal("42000"))
        .time(1700000000000L)
        .tradeId(12345L)
        .orderId(67890L)
        .fee(new BigDecimal("5.5"))
        .isTaker(true)
        .rawJson("{\"futures\":true}")
        .build();

    UserTrade trade = CoincallAdapters.toUserTrade(dto);

    assertNotNull(trade);

    // BUY -> BID
    assertEquals(OrderType.BID, trade.getType());

    // Futures instrument fields
    FuturesContract instrument = (FuturesContract) trade.getInstrument();
    assertEquals(new CurrencyPair("BTC", "USD"), instrument.getCurrencyPair());
    assertEquals("PERP", instrument.getPrompt());

    assertEquals(new BigDecimal("3"), trade.getOriginalAmount());
    assertEquals(new BigDecimal("42000"), trade.getPrice());
    assertEquals(new Date(1700000000000L), trade.getTimestamp());
    assertEquals("12345", trade.getId());
    assertEquals("67890", trade.getOrderId());

    assertEquals(new BigDecimal("5.5"), trade.getFeeAmount());
    assertEquals(Currency.USD, trade.getFeeCurrency());

    assertEquals(MarketParticipant.TAKER, trade.getMarketParticipant());
    assertEquals("{\"futures\":true}", trade.getRawJson());
  }

  @Test
  public void testToUserTradeFutures_sellMaker() {
    CoincallFuturesTransactionDetail dto = CoincallFuturesTransactionDetail.builder()
        .tradeSide(CoincallTradeSide.SELL)
        .displayName("ETH-QUARTER")
        .symbol("ETHUSD")
        .qty(new BigDecimal("1.25"))
        .price(new BigDecimal("2500"))
        .time(1700001000000L)
        .tradeId(999L)
        .orderId(111L)
        .fee(new BigDecimal("0.75"))
        .isTaker(false)
        .rawJson("{\"side\":\"sell\"}")
        .build();

    UserTrade trade = CoincallAdapters.toUserTrade(dto);

    assertNotNull(trade);

    // SELL -> ASK
    assertEquals(OrderType.ASK, trade.getType());

    FuturesContract instrument = (FuturesContract) trade.getInstrument();
    assertEquals(new CurrencyPair("ETH", "USD"), instrument.getCurrencyPair());
    assertEquals("QUARTER", instrument.getPrompt());

    assertEquals(new BigDecimal("1.25"), trade.getOriginalAmount());
    assertEquals(new BigDecimal("2500"), trade.getPrice());
    assertEquals(new Date(1700001000000L), trade.getTimestamp());
    assertEquals("999", trade.getId());
    assertEquals("111", trade.getOrderId());

    assertEquals(new BigDecimal("0.75"), trade.getFeeAmount());
    assertEquals(Currency.USD, trade.getFeeCurrency());

    assertEquals(MarketParticipant.MAKER, trade.getMarketParticipant());

    assertEquals("{\"side\":\"sell\"}", trade.getRawJson());
  }

  @Test
  public void testToUserTradeOptions_buyTaker() {
    CoincallOptionTransactionDetail dto = CoincallOptionTransactionDetail.builder()
        .tradeSide(CoincallTradeSide.BUY)
        .displayName("BTC-3MAR24-50000-C")
        .qty(new BigDecimal("1.5"))
        .price(new BigDecimal("2500"))
        .time(1700000000000L)
        .tradeId(12345L)
        .orderId(67890L)
        .fee(new BigDecimal("3"))
        .isTaker(true)
        .rawJson("{\"ok\":true}")
        .build();

    UserTrade trade = CoincallAdapters.toUserTrade(dto);

    assertNotNull(trade);

    // BUY → BID
    assertEquals(OrderType.BID, trade.getType());

    // Instrument from toOptionsContract()
    OptionsContract instrument = (OptionsContract) trade.getInstrument();
    assertEquals(new CurrencyPair("BTC", "USD"), instrument.getCurrencyPair());
    assertEquals(OptionType.CALL, instrument.getType());
    assertEquals(new BigDecimal("50000"), instrument.getStrike());

    // Core fields
    assertEquals(new BigDecimal("1.5"), trade.getOriginalAmount());
    assertEquals(new BigDecimal("2500"), trade.getPrice());
    assertEquals(new Date(1700000000000L), trade.getTimestamp());
    assertEquals("12345", trade.getId());
    assertEquals("67890", trade.getOrderId());

    // Fee fields
    assertEquals(new BigDecimal("3"), trade.getFeeAmount());
    assertEquals(Currency.USD, trade.getFeeCurrency());

    // Taker flag
    assertEquals(MarketParticipant.TAKER, trade.getMarketParticipant());

    assertEquals("{\"ok\":true}", trade.getRawJson());
  }

  @Test
  public void testToUserTradeOptions_sellMaker() {
    CoincallOptionTransactionDetail dto = CoincallOptionTransactionDetail.builder()
        .tradeSide(CoincallTradeSide.SELL)
        .displayName("ETHUSD-15DEC25-2000-P")
        .qty(new BigDecimal("0.25"))
        .price(new BigDecimal("150"))
        .time(1700001000000L)
        .tradeId(999L)
        .orderId(111L)
        .fee(new BigDecimal("0.5"))
        .isTaker(false)
        .rawJson("{\"side\":\"sell\"}")
        .build();

    UserTrade trade = CoincallAdapters.toUserTrade(dto);

    assertNotNull(trade);

    // SELL → ASK
    assertEquals(OrderType.ASK, trade.getType());

    // Instrument (base should strip "USD")
    OptionsContract instrument = (OptionsContract) trade.getInstrument();
    assertEquals(new CurrencyPair("ETH", "USD"), instrument.getCurrencyPair());
    assertEquals(OptionType.PUT, instrument.getType());
    assertEquals(new BigDecimal("2000"), instrument.getStrike());

    assertEquals(new BigDecimal("0.25"), trade.getOriginalAmount());
    assertEquals(new BigDecimal("150"), trade.getPrice());
    assertEquals(new Date(1700001000000L), trade.getTimestamp());
    assertEquals("999", trade.getId());
    assertEquals("111", trade.getOrderId());

    assertEquals(new BigDecimal("0.5"), trade.getFeeAmount());
    assertEquals(Currency.USD, trade.getFeeCurrency());

    // Maker flag
    assertEquals(MarketParticipant.MAKER, trade.getMarketParticipant());

    assertEquals("{\"side\":\"sell\"}", trade.getRawJson());
  }

  @Test
  public void testToUserTradeSpot_buyTaker() {
    CoincallSpotFillDto dto = CoincallSpotFillDto.builder()
        .tradeSide(CoincallTradeSide.BUY)
        .displaySymbol("BTC/USDT")
        .quantity(new BigDecimal("0.25"))
        .price(new BigDecimal("42000"))
        .timestamp(1700000000000L)
        .tradeId("T123")
        .orderId(98765L)
        .fee(new BigDecimal("0.0005"))
        .feeCurrency("USDT")
        .isTaker(true)
        .clientOrderId("client-1")
        .rawJson("{\"spot\":true}")
        .build();

    UserTrade trade = CoincallAdapters.toUserTrade(dto);

    assertNotNull(trade);

    // BUY → BID
    assertEquals(OrderType.BID, trade.getType());

    // Instrument from toCurrencyPair
    CurrencyPair pair = (CurrencyPair) trade.getInstrument();
    assertEquals(new CurrencyPair("BTC", "USDT"), pair);

    // Core fields
    assertEquals(new BigDecimal("0.25"), trade.getOriginalAmount());
    assertEquals(new BigDecimal("42000"), trade.getPrice());
    assertEquals(new Date(1700000000000L), trade.getTimestamp());
    assertEquals("T123", trade.getId());
    assertEquals("98765", trade.getOrderId());

    // Fee
    assertEquals(new BigDecimal("0.0005"), trade.getFeeAmount());
    assertEquals(new Currency("USDT"), trade.getFeeCurrency());

    // Taker
    assertEquals(MarketParticipant.TAKER, trade.getMarketParticipant());

    // Client order reference
    assertEquals("client-1", trade.getOrderUserReference());

    assertEquals("{\"spot\":true}", trade.getRawJson());
  }

  @Test
  public void testToUserTradeSpot_sellMaker() {
    CoincallSpotFillDto dto = CoincallSpotFillDto.builder()
        .tradeSide(CoincallTradeSide.SELL)
        .displaySymbol("ETH/USDT")
        .quantity(new BigDecimal("1.5"))
        .price(new BigDecimal("2500"))
        .timestamp(1700001000000L)
        .tradeId("T999")
        .orderId(44444L)
        .fee(new BigDecimal("1.2"))
        .feeCurrency("ETH")
        .isTaker(false)
        .clientOrderId("client-xyz")
        .rawJson("{\"side\":\"sell\"}")
        .build();

    UserTrade trade = CoincallAdapters.toUserTrade(dto);

    assertNotNull(trade);

    // SELL → ASK
    assertEquals(OrderType.ASK, trade.getType());

    CurrencyPair pair = (CurrencyPair) trade.getInstrument();
    assertEquals(new CurrencyPair("ETH", "USDT"), pair);

    assertEquals(new BigDecimal("1.5"), trade.getOriginalAmount());
    assertEquals(new BigDecimal("2500"), trade.getPrice());
    assertEquals(new Date(1700001000000L), trade.getTimestamp());
    assertEquals("T999", trade.getId());
    assertEquals("44444", trade.getOrderId());

    assertEquals(new BigDecimal("1.2"), trade.getFeeAmount());
    assertEquals(new Currency("ETH"), trade.getFeeCurrency());

    // Maker
    assertEquals(MarketParticipant.MAKER, trade.getMarketParticipant());

    assertEquals("client-xyz", trade.getOrderUserReference());
    assertEquals("{\"side\":\"sell\"}", trade.getRawJson());
  }
}
