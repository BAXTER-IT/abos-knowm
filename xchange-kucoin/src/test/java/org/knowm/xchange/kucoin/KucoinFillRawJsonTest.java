package org.knowm.xchange.kucoin;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.enums.MarketParticipant;
import org.knowm.xchange.kucoin.dto.response.KucoinResponse;
import org.knowm.xchange.kucoin.dto.response.Pagination;
import org.knowm.xchange.kucoin.dto.response.TradeResponse;

/**
 * A fill row must reach the caller as the venue sent it: the typed DTO only knows the spot fields,
 * and the futures host's rows carry more (tradeType, settleCurrency, tradeTime, ...).
 */
public class KucoinFillRawJsonTest {

  private static final String FUTURES_FILLS = "/org/knowm/xchange/kucoin/dto/trade/futures-fills.json";

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void futuresFillKeepsTheRowAsReceived() throws IOException {
    JsonNode page = readFixture();
    List<TradeResponse> fills = parse(page).getData().getItems();

    assertThat(fills).hasSize(2);
    for (int i = 0; i < fills.size(); i++) {
      JsonNode sent = page.at("/data/items/" + i);
      // Same content, same field order, nothing dropped: the futures-only fields survive.
      assertThat(mapper.readTree(fills.get(i).getRawJson())).isEqualTo(sent);
      assertThat(fills.get(i).getRawJson())
          .contains("\"tradeType\":\"" + sent.get("tradeType").asText() + "\"")
          .contains("\"settleCurrency\":\"USDT\"")
          .contains("\"tradeTime\":" + sent.get("tradeTime").asLong())
          .contains("\"marginMode\":\"" + sent.get("marginMode").asText() + "\"");
    }
    assertThat(fills.get(1).getRawJson()).contains("\"tradeType\":\"liquidation\"");
  }

  @Test
  public void typedFieldsStillMapped() throws IOException {
    TradeResponse fill = parse(readFixture()).getData().getItems().get(0);

    assertThat(fill.getSymbol()).isEqualTo("XBTUSDTM");
    assertThat(fill.getTradeId()).isEqualTo("1828954878212");
    assertThat(fill.getOrderId()).isEqualTo("284486580251463680");
    assertThat(fill.getSide()).isEqualTo("buy");
    assertThat(fill.getLiquidity()).isEqualTo(TradeResponse.Liquidity.TAKER);
    assertThat(fill.getPrice()).isEqualByComparingTo(new BigDecimal("86275.1"));
    assertThat(fill.getSize()).isEqualByComparingTo(BigDecimal.ONE);
    assertThat(fill.getFee()).isEqualByComparingTo(new BigDecimal("0.05176506"));
    assertThat(fill.getFeeCurrency()).isEqualTo("USDT");
    assertThat(fill.getOrderType()).isEqualTo("market");
    assertThat(fill.getTradeCreatedAt()).isEqualTo(new Date(1740640088427L));
  }

  @Test
  public void futuresFillAdaptsToUserTradeWithRawJson() throws IOException {
    TradeResponse fill = parse(readFixture()).getData().getItems().get(1);

    UserTrade trade = KucoinAdapters.adaptUserTrade(fill);

    assertThat(trade.getRawJson()).isEqualTo(fill.getRawJson());
    assertThat(trade.getId()).isEqualTo("1828954878300");
    assertThat(trade.getOrderId()).isEqualTo("284486580251463999");
    assertThat(trade.getType()).isEqualTo(OrderType.ASK);
    assertThat(trade.getTimestamp()).isEqualTo(new Date(1740650000456L));
    assertThat(trade.getMarketParticipant()).isEqualTo(MarketParticipant.TAKER);
    assertThat(trade.getFeeCurrency()).isEqualTo(Currency.USDT);
    // A contract name is not a BASE-QUOTE pair; it is still on rawJson.
    assertThat(trade.getInstrument()).isNull();
  }

  @Test
  public void spotFillUnchangedAndCarriesRawJson() throws IOException {
    String row =
        "{\"symbol\":\"BTC-USDT\",\"tradeId\":\"5c35c02709e4f67d5266954e\",\"orderId\":\"5c35c02709e4f67d5266954d\","
            + "\"counterOrderId\":\"5c1ab46003aa676e487fa8e3\",\"side\":\"buy\",\"liquidity\":\"taker\",\"forceTaker\":true,"
            + "\"price\":\"0.083\",\"size\":\"0.8424304\",\"funds\":\"0.0699217232\",\"fee\":\"0\",\"feeRate\":\"0\","
            + "\"feeCurrency\":\"USDT\",\"stop\":\"\",\"type\":\"limit\",\"createdAt\":1547026472000,\"tradeType\":\"TRADE\"}";
    TradeResponse fill = mapper.readValue(row, TradeResponse.class);

    UserTrade trade = KucoinAdapters.adaptUserTrade(fill);

    assertThat(trade.getInstrument()).isEqualTo(CurrencyPair.BTC_USDT);
    assertThat(trade.getOriginalAmount()).isEqualByComparingTo(new BigDecimal("0.8424304"));
    assertThat(trade.getPrice()).isEqualByComparingTo(new BigDecimal("0.083"));
    assertThat(fill.getOrderType()).isEqualTo("limit");
    assertThat(trade.getRawJson()).isEqualTo(row);
  }

  private JsonNode readFixture() throws IOException {
    try (InputStream in = getClass().getResourceAsStream(FUTURES_FILLS)) {
      return mapper.readTree(in);
    }
  }

  /** The same envelope the REST proxy deserialises. */
  private KucoinResponse<Pagination<TradeResponse>> parse(JsonNode page) throws IOException {
    return mapper.readValue(
        mapper.treeAsTokens(page), new TypeReference<KucoinResponse<Pagination<TradeResponse>>>() {});
  }
}
