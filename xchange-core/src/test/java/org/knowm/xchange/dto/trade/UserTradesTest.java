package org.knowm.xchange.dto.trade;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.enums.MarketParticipant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserTradesTest {

  @Test
  public void userTradesJsonMarchallTest() throws JsonProcessingException {

    List<UserTrade> userTradeList = new ArrayList<>();
    userTradeList.add(
        new UserTrade.Builder()
            .timestamp(Date.from(Instant.now()))
            .currencyPair(CurrencyPair.BTC_USD)
            .price(BigDecimal.ONE)
            .originalAmount(BigDecimal.ONE)
            .marketParticipant(MarketParticipant.MAKER)
            .build());
    userTradeList.add(
        new UserTrade.Builder()
            .timestamp(Date.from(Instant.now()))
            .currencyPair(CurrencyPair.BTC_USD)
            .id("id")
            .price(BigDecimal.ONE)
            .originalAmount(BigDecimal.ONE)
            .marketParticipant(MarketParticipant.TAKER)
            .build());
    userTradeList.add(
        new UserTrade.Builder()
            .timestamp(Date.from(Instant.now()))
            .currencyPair(CurrencyPair.BTC_USD)
            .id("id")
            .instrument(CurrencyPair.BTC_USD)
            .price(BigDecimal.ONE)
            .originalAmount(BigDecimal.ONE)
            .marketParticipant("taker")
            .build());

    UserTrades userTrades = new UserTrades(userTradeList, Trades.TradeSortType.SortByTimestamp);

    String json = new ObjectMapper().writeValueAsString(userTrades);

    UserTrades result = new ObjectMapper().readValue(json, UserTrades.class);

    assertThat(result).isInstanceOf(UserTrades.class);
    assertThat(result.getUserTrades().get(0)).isInstanceOf(UserTrade.class);
    System.out.println(result);
  }
}
