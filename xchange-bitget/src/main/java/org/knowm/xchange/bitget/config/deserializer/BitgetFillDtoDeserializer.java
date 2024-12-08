package org.knowm.xchange.bitget.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import org.knowm.xchange.bitget.config.converter.StringToOrderTypeConverter;
import org.knowm.xchange.bitget.dto.trade.BitgetFillDto;
import org.knowm.xchange.bitget.dto.trade.BitgetFillDto.FeeDetail;
import org.knowm.xchange.bitget.dto.trade.BitgetFillDto.OrderType;
import org.knowm.xchange.bitget.dto.trade.BitgetFillDto.TradeScope;
import org.knowm.xchange.dto.Order;

public class BitgetFillDtoDeserializer extends JsonDeserializer<BitgetFillDto> {

  private static final ObjectMapper om = new ObjectMapper();

  @Override
  public BitgetFillDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

    JsonNode node = p.getCodec().readTree(p);

    String sideString = node.get("side").asText();
    StringToOrderTypeConverter converter = new StringToOrderTypeConverter();
    Order.OrderType orderSide = converter.convert(sideString);

    JsonParser feeDetailParser = node.get("feeDetail").traverse(p.getCodec());
    FeeDetail feeDetail = om.readValue(feeDetailParser, FeeDetail.class);

    JsonParser tradeScopeParser = node.get("tradeScope").traverse(p.getCodec());
    TradeScope tradeScope = om.readValue(tradeScopeParser, TradeScope.class);

    return BitgetFillDto.builder()
        .acccountId(node.get("userId").asText())
        .symbol(node.get("symbol").asText())
        .orderId(node.get("orderId").asText())
        .tradeId(node.get("tradeId").asText())
        .orderType(OrderType.forValue(node.get("orderType").asText()))
        .orderSide(orderSide)
        .price(node.get("priceAvg").decimalValue())
        .assetAmount(node.get("size").decimalValue())
        .quoteAmount(node.get("amount").decimalValue())
        .feeDetail(feeDetail)
        .tradeScope(tradeScope)
        .createdAt(Instant.ofEpochMilli(node.get("cTime").asLong()))
        .updatedAt(Instant.ofEpochMilli(node.get("uTime").asLong()))
        .rawJson(node.toString())
        .build();
  }
}
