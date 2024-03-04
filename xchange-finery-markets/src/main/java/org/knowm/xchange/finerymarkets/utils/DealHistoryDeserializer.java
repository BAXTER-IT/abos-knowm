package org.knowm.xchange.finerymarkets.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.knowm.xchange.finerymarkets.dto.CancelReason;
import org.knowm.xchange.finerymarkets.dto.OrderType;
import org.knowm.xchange.finerymarkets.dto.Side;
import org.knowm.xchange.finerymarkets.dto.trade.DealHistory;
import org.knowm.xchange.finerymarkets.dto.trade.OrderCreatedBy;

public class DealHistoryDeserializer extends JsonDeserializer<DealHistory> {

  @Override
  public DealHistory deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);

    return DealHistory.builder()
        .instrumentName(node.get(0).asText())
        .orderType(OrderType.values()[node.get(1).asInt()])
        .side(Side.values()[node.get(2).asInt()])
        .cancelReason(CancelReason.values()[node.get(3).asInt()])
        .orderId(node.get(4).asLong())
        .clientOrderId(node.get(5).asInt())
        .orderPrice(node.get(6).asLong())
        .orderInitialSize(node.get(7).asLong())
        .remainingOrderSize(node.get(8).asLong())
        .orderCreatedAt(node.get(9).asLong())
        .dealMoment(node.get(10).asLong())
        .dealId(node.get(11).asInt())
        .dealAggressorSide(Side.values()[node.get(12).asInt()])
        .dealPrice(node.get(13).asLong())
        .dealSize(node.get(14).asLong())
        .dealVolume(node.get(15).asLong())
        .dealDelta(node.get(16).asLong())
        .counterpartyId(node.get(17).asInt())
        .bySizeOrVolume(OrderCreatedBy.values()[node.get(18).asInt()])
        .counterpartySubaccountId(node.get(19).asInt())
        .linkedDealId(node.get(20).asInt())
        .rawData(node.toString())
        .build();
  }
}
