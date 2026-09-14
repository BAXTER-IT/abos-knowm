package org.knowm.xchange.gemini.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.knowm.xchange.gemini.dto.trade.GeminiTransactionTradeResponse;
import org.knowm.xchange.gemini.utils.DeserializationUtil;

public class GeminiTransactionTradeResponseDeserializer extends JsonDeserializer<GeminiTransactionTradeResponse> {

  @Override
  public GeminiTransactionTradeResponse deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    String rawJson = node.toString();

    return GeminiTransactionTradeResponse.builder()
        .account(DeserializationUtil.getText(node, "account"))
        .amount(DeserializationUtil.getBigDecimal(node, "amount"))
        .price(DeserializationUtil.getBigDecimal(node, "price"))
        .timestampMs(DeserializationUtil.getLong(node, "timestampms"))
        .side(DeserializationUtil.getText(node, "side"))
        .isAggressor(DeserializationUtil.getBoolean(node, "isAggressor"))
        .feeAssetCode(DeserializationUtil.getText(node, "feeAssetCode"))
        .feeAmount(DeserializationUtil.getBigDecimal(node, "feeAmount"))
        .orderId(DeserializationUtil.getLong(node, "orderId"))
        .exchange(DeserializationUtil.getText(node, "exchange"))
        .isAuctionFill(DeserializationUtil.getBoolean(node, "isAuctionFill"))
        .isClearingFill(DeserializationUtil.getBoolean(node, "isClearingFill"))
        .symbol(DeserializationUtil.getText(node, "symbol"))
        .type(DeserializationUtil.getText(node, "type"))
        .rawJson(rawJson)
        .build();
  }
}
