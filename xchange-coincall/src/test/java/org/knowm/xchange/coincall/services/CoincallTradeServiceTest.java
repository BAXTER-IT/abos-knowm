package org.knowm.xchange.coincall.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coincall.CoincallExchange;
import org.knowm.xchange.coincall.dtos.enums.CoincallTradeSide;
import org.knowm.xchange.coincall.dtos.trade.CoincallFuturesTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallOptionTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallSpotFillDto;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CoincallTradeServiceTest {

  @Test
  public void testGetSpotUserTrades_buildsSymbolOrderIdAndTimeSpan() throws IOException {
    ExchangeSpecification specification = new ExchangeSpecification(CoincallExchange.class);
    specification.setSslUri("http://test.com");
    specification.setApiKey("test-api-key");
    specification.setSecretKey("test-secret-key");
    CoincallExchange exchange = mock(CoincallExchange.class);
    when(exchange.getExchangeSpecification()).thenReturn(specification);
    CoincallTradeService service = spy(new CoincallTradeService(exchange));

    TradeHistoryParamsAll params = new TradeHistoryParamsAll();
    params.setInstrument(new CurrencyPair("BTC", "USDT")); // -> "BTC/USDT" -> "BTCUSDT"
    params.setOrderId("123");
    Date start = new Date(1700000000000L);
    Date end = new Date(1700001000000L);
    params.setStartTime(start);
    params.setEndTime(end);

    long expectedStart = start.getTime();
    long expectedEnd = end.getTime();

    CoincallSpotFillDto fill =
        CoincallSpotFillDto.builder()
            .tradeSide(CoincallTradeSide.BUY)
            .displaySymbol("BTC/USDT")
            .quantity(new java.math.BigDecimal("0.1"))
            .price(new java.math.BigDecimal("40000"))
            .timestamp(1700000000000L)
            .tradeId("spot-1")
            .orderId(123L)
            .fee(new java.math.BigDecimal("0.0001"))
            .feeCurrency("USDT")
            .isTaker(true)
            .clientOrderId("client-1")
            .rawJson("{\"spot\":true}")
            .build();

    // Verify selector logic: symbol "BTCUSDT", orderId 123L, start/end times
    doReturn(Arrays.asList(fill))
        .when(service)
        .getCoincallSpotFills("BTCUSDT", 123L, expectedStart, expectedEnd);

    List<UserTrade> trades = service.getSpotUserTrades(params);

    assertEquals(1, trades.size());
    UserTrade t = trades.get(0);
    assertEquals("spot-1", t.getId());
    assertEquals(new CurrencyPair("BTC", "USDT"), (CurrencyPair) t.getInstrument());
  }

  @Test
  public void testGetSpotUserTrades_nullInstrumentAndOrderId() throws IOException {
    ExchangeSpecification specification = new ExchangeSpecification(CoincallExchange.class);
    specification.setSslUri("http://test.com");
    specification.setApiKey("test-api-key");
    specification.setSecretKey("test-secret-key");
    CoincallExchange exchange = mock(CoincallExchange.class);
    when(exchange.getExchangeSpecification()).thenReturn(specification);    CoincallTradeService service = spy(new CoincallTradeService(exchange));

    TradeHistoryParamsAll params = new TradeHistoryParamsAll();
    // instrument and orderId left null
    Date start = new Date(1700000000000L);
    Date end = new Date(1700001000000L);
    params.setStartTime(start);
    params.setEndTime(end);

    long expectedStart = start.getTime();
    long expectedEnd = end.getTime();

    CoincallSpotFillDto fill =
        CoincallSpotFillDto.builder()
            .tradeSide(CoincallTradeSide.SELL)
            .displaySymbol("ETH/USDT")
            .quantity(new java.math.BigDecimal("1.0"))
            .price(new java.math.BigDecimal("2500"))
            .timestamp(1700000000000L)
            .tradeId("spot-2")
            .orderId(999L)
            .fee(new java.math.BigDecimal("0.01"))
            .feeCurrency("USDT")
            .isTaker(false)
            .clientOrderId("client-2")
            .rawJson("{\"spot\":2}")
            .build();

    // symbol and orderId should both be null
    doReturn(Arrays.asList(fill))
        .when(service)
        .getCoincallSpotFills(null, null, expectedStart, expectedEnd);

    List<UserTrade> trades = service.getSpotUserTrades(params);

    assertEquals(1, trades.size());
    UserTrade t = trades.get(0);
    assertEquals("spot-2", t.getId());
    assertEquals(new CurrencyPair("ETH", "USDT"), (CurrencyPair) t.getInstrument());
  }

  @Test
  public void testGetFuturesUserTrades_usesTimeSpanFromParamsAll() throws IOException {
    ExchangeSpecification specification = new ExchangeSpecification(CoincallExchange.class);
    specification.setSslUri("http://test.com");
    specification.setApiKey("test-api-key");
    specification.setSecretKey("test-secret-key");
    CoincallExchange exchange = mock(CoincallExchange.class);
    when(exchange.getExchangeSpecification()).thenReturn(specification);    CoincallTradeService service = spy(new CoincallTradeService(exchange));

    TradeHistoryParamsAll params = new TradeHistoryParamsAll();
    Date start = new Date(1700000000000L);
    Date end = new Date(1700001000000L);
    params.setStartTime(start);
    params.setEndTime(end);

    long expectedStart = start.getTime();
    long expectedEnd = end.getTime();

    CoincallFuturesTransactionDetail detail1 =
        CoincallFuturesTransactionDetail.builder()
            .tradeSide(CoincallTradeSide.BUY)
            .displayName("BTC-PERP")
            .symbol("BTCUSD")
            .qty(new java.math.BigDecimal("1"))
            .price(new java.math.BigDecimal("41000"))
            .time(1700000000000L)
            .tradeId(1L)
            .orderId(101L)
            .fee(new java.math.BigDecimal("5"))
            .isTaker(true)
            .rawJson("{\"futures\":1}")
            .build();

    CoincallFuturesTransactionDetail detail2 =
        CoincallFuturesTransactionDetail.builder()
            .tradeSide(CoincallTradeSide.SELL)
            .displayName("ETH-PERP")
            .symbol("ETHUSD")
            .qty(new java.math.BigDecimal("2"))
            .price(new java.math.BigDecimal("2500"))
            .time(1700000500000L)
            .tradeId(2L)
            .orderId(102L)
            .fee(new java.math.BigDecimal("3"))
            .isTaker(false)
            .rawJson("{\"futures\":2}")
            .build();

    doReturn(Arrays.asList(detail1, detail2))
        .when(service)
        .getAllFuturesTransactionDetails(expectedStart, expectedEnd);

    List<UserTrade> trades = service.getFuturesUserTrades(params);

    assertEquals(2, trades.size());
    assertEquals("1", trades.get(0).getId());
    assertEquals("2", trades.get(1).getId());
  }

  @Test
  public void testGetOptionsUserTrades_usesTimeSpanFromParamsAll() throws IOException {
    ExchangeSpecification specification = new ExchangeSpecification(CoincallExchange.class);
    specification.setSslUri("http://test.com");
    specification.setApiKey("test-api-key");
    specification.setSecretKey("test-secret-key");
    CoincallExchange exchange = mock(CoincallExchange.class);
    when(exchange.getExchangeSpecification()).thenReturn(specification);    CoincallTradeService service = spy(new CoincallTradeService(exchange));

    TradeHistoryParamsAll params = new TradeHistoryParamsAll();
    Date start = new Date(1700000000000L);
    Date end = new Date(1700001000000L);
    params.setStartTime(start);
    params.setEndTime(end);

    long expectedStart = start.getTime();
    long expectedEnd = end.getTime();

    CoincallOptionTransactionDetail opt1 =
        CoincallOptionTransactionDetail.builder()
            .tradeSide(CoincallTradeSide.BUY)
            .displayName("BTC-3MAR24-50000-C")
            .qty(new java.math.BigDecimal("0.5"))
            .price(new java.math.BigDecimal("2000"))
            .time(1700000000000L)
            .tradeId(10L)
            .orderId(201L)
            .fee(new java.math.BigDecimal("1"))
            .isTaker(true)
            .rawJson("{\"opt\":1}")
            .build();

    CoincallOptionTransactionDetail opt2 =
        CoincallOptionTransactionDetail.builder()
            .tradeSide(CoincallTradeSide.SELL)
            .displayName("ETHUSD-15DEC25-2000-P")
            .qty(new java.math.BigDecimal("1"))
            .price(new java.math.BigDecimal("150"))
            .time(1700000500000L)
            .tradeId(11L)
            .orderId(202L)
            .fee(new java.math.BigDecimal("0.5"))
            .isTaker(false)
            .rawJson("{\"opt\":2}")
            .build();

    doReturn(Arrays.asList(opt1, opt2))
        .when(service)
        .getAllOptionTransactionDetails(expectedStart, expectedEnd);

    List<UserTrade> trades = service.getOptionsUserTrades(params);

    assertEquals(2, trades.size());
    assertEquals("10", trades.get(0).getId());
    assertEquals("11", trades.get(1).getId());
  }


  @Test
  public void testGetTradeHistory_aggregatesSpotFuturesOptions() throws IOException {
    ExchangeSpecification specification = new ExchangeSpecification(CoincallExchange.class);
    specification.setSslUri("http://test.com");
    specification.setApiKey("test-api-key");
    specification.setSecretKey("test-secret-key");
    CoincallExchange exchange = mock(CoincallExchange.class);
    when(exchange.getExchangeSpecification()).thenReturn(specification);
    CoincallTradeService service = spy(new CoincallTradeService(exchange));

    TradeHistoryParams params = new TradeHistoryParamsAll();

    UserTrade spotTrade = new UserTrade.Builder()
        .id("spot")
        .timestamp(new Date(1))
        .build();
    UserTrade futuresTrade = new UserTrade.Builder()
        .id("futures")
        .timestamp(new Date(2))
        .build();
    UserTrade optionsTrade = new UserTrade.Builder()
        .id("options")
        .timestamp(new Date(3))
        .build();

    doReturn(Stream.of(spotTrade).collect(Collectors.toList()))
        .when(service)
        .getSpotUserTrades(params);
    doReturn(Stream.of(futuresTrade).collect(Collectors.toList()))
        .when(service)
        .getFuturesUserTrades(params);
    doReturn(Stream.of(optionsTrade).collect(Collectors.toList()))
        .when(service)
        .getOptionsUserTrades(params);

    UserTrades result = service.getTradeHistory(params);

    assertNotNull(result);
    assertEquals(3, result.getUserTrades().size());
  }

}