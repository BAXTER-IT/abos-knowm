package info.bitrich.xchangestream.gemini.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.gemini.dto.enums.GeminiOrderEventType;
import info.bitrich.xchangestream.gemini.dto.enums.GeminiWsTradeSide;
import info.bitrich.xchangestream.gemini.dto.trade.GeminiWsTradeFillDto;
import info.bitrich.xchangestream.gemini.dto.trade.GeminiWsTradeResponse;
import java.io.IOException;
import org.knowm.xchange.gemini.utils.DeserializationUtil;

public class GeminiWsTradeResponseDeserializer  extends JsonDeserializer<GeminiWsTradeResponse> {

  @Override
  public GeminiWsTradeResponse deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {

    JsonNode node = p.getCodec().readTree(p);
    String rawJson = node.toString();

    String typeString = DeserializationUtil.getText(node, "type");
    GeminiOrderEventType type = null;
    if (typeString != null) {
      type = GeminiOrderEventType.fromExchangeValue(typeString);
    }

    String sideString = DeserializationUtil.getText(node, "side");
    GeminiWsTradeSide side = null;
    if (sideString != null) {
      side = GeminiWsTradeSide.fromExchangeValue(sideString);
    }

    JsonNode fillNode = node.get("fill");
    GeminiWsTradeFillDto fill = null;
    if (fillNode != null && !fillNode.isNull()) {
      fill = GeminiWsTradeFillDto.builder()
          .tradeId(DeserializationUtil.getText(fillNode, "trade_id"))
          .liquidity(DeserializationUtil.getText(fillNode, "liquidity"))
          .price(DeserializationUtil.getBigDecimal(fillNode, "price"))
          .amount(DeserializationUtil.getBigDecimal(fillNode, "amount"))
          .fee(DeserializationUtil.getBigDecimal(fillNode, "fee"))
          .feeCurrency(DeserializationUtil.getText(fillNode, "fee_currency"))
          .build();
    }

    return GeminiWsTradeResponse.builder()
        .type(type)
        .socketSequence(DeserializationUtil.getLong(node, "socket_sequence"))
        .orderId(DeserializationUtil.getText(node, "order_id"))
        .eventId(DeserializationUtil.getText(node, "event_id"))
        .accountName(DeserializationUtil.getText(node, "account_name"))
        .apiSession(DeserializationUtil.getText(node, "api_session"))
        .clientOrderId(DeserializationUtil.getText(node, "client_order_id"))
        .symbol(DeserializationUtil.getText(node, "symbol"))
        .side(side)
        .behavior(DeserializationUtil.getText(node, "behavior"))
        .orderType(DeserializationUtil.getText(node, "order_type"))
        .timestamp(DeserializationUtil.getText(node, "timestamp"))
        .timestampms(DeserializationUtil.getLong(node, "timestampms"))
        .isLive(DeserializationUtil.getBoolean(node, "is_live"))
        .isCancelled(DeserializationUtil.getBoolean(node, "is_cancelled"))
        .isHidden(DeserializationUtil.getBoolean(node, "is_hidden"))
        .avgExecutionPrice(DeserializationUtil.getBigDecimal(node, "avg_execution_price"))
        .executedAmount(DeserializationUtil.getBigDecimal(node, "executed_amount"))
        .remainingAmount(DeserializationUtil.getBigDecimal(node, "remaining_amount"))
        .originalAmount(DeserializationUtil.getBigDecimal(node, "original_amount"))
        .price(DeserializationUtil.getBigDecimal(node, "price"))
        .totalSpend(DeserializationUtil.getBigDecimal(node, "total_spend"))
        .fill(fill)
        .rawJson(rawJson)
        .build();
  }
}
