package org.knowm.xchange.gemini.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.knowm.xchange.gemini.dto.enums.GeminiTradeBreak;
import org.knowm.xchange.gemini.dto.enums.GeminiTradeType;
import org.knowm.xchange.gemini.dto.trade.GeminiTradeResponse;
import org.knowm.xchange.gemini.utils.DeserializationUtil;

public class GeminiTradeResponseDeserializer extends JsonDeserializer<GeminiTradeResponse> {

  @Override
  public GeminiTradeResponse deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    String rawJson = node.toString();

    String tradeTypeString = DeserializationUtil.getText(node, "type");
    GeminiTradeType tradeType = null;
    if (tradeTypeString != null) {
      tradeType = GeminiTradeType.fromExchangeValue(
          tradeTypeString);
    }

    String tradeBreakString = DeserializationUtil.getText(node, "break");
    GeminiTradeBreak tradeBreak = null;
    if (tradeBreakString != null) {
      tradeBreak = GeminiTradeBreak.fromExchangeValue(
          tradeBreakString);
    }

    return GeminiTradeResponse.builder()
        .price(DeserializationUtil.getBigDecimal(node, "price"))
        .amount(DeserializationUtil.getBigDecimal(node, "amount"))
        .symbol(DeserializationUtil.getText(node, "symbol"))
        .timestamp(DeserializationUtil.getLong(node, "timestamp"))
        .timestampms(DeserializationUtil.getLong(node, "timestampms"))
        .type(tradeType)
        .aggressor(DeserializationUtil.getBoolean(node, "aggressor"))
        .feeCurrency(DeserializationUtil.getText(node, "fee_currency"))
        .feeAmount(DeserializationUtil.getBigDecimal(node, "fee_amount"))
        .tradeId(DeserializationUtil.getInt(node, "tid"))
        .orderId(DeserializationUtil.getText(node, "order_id"))
        .clientOrderId(DeserializationUtil.getText(node, "client_order_id"))
        .exchange(DeserializationUtil.getText(node, "exchange"))
        .isAuctionFill(DeserializationUtil.getBoolean(node, "is_auction_fill"))
        .breakValue(tradeBreak)
        .rawJson(rawJson)
        .build();
  }
}
