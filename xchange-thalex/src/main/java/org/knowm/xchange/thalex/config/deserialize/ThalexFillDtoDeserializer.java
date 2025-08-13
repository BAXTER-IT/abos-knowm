package org.knowm.xchange.thalex.config.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.knowm.xchange.thalex.dto.enums.ThalexDirection;
import org.knowm.xchange.thalex.dto.enums.ThalexMakerTaker;
import org.knowm.xchange.thalex.dto.enums.ThalexTradeType;
import org.knowm.xchange.thalex.dto.trade.ThalexTradeDto;
import org.knowm.xchange.thalex.utils.DeserializationUtil;

public class ThalexFillDtoDeserializer extends JsonDeserializer<ThalexTradeDto> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public ThalexTradeDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    String rawJson = node.toString();

    return ThalexTradeDto.builder()
        .tradeType(objectMapper.convertValue(node.get("trade_type"), ThalexTradeType.class))
        .tradeId(DeserializationUtil.getText(node, "trade_id"))
        .orderId(DeserializationUtil.getText(node, "order_id"))
        .instrumentName(DeserializationUtil.getText(node, "instrument_name"))
        .direction(objectMapper.convertValue(node.get("direction"), ThalexDirection.class))
        .price(DeserializationUtil.getBigDecimal(node, "price"))
        .amount(DeserializationUtil.getBigDecimal(node, "amount"))
        .label(DeserializationUtil.getText(node, "label"))
        .time(DeserializationUtil.getInstant(node))
        .positionAfter(DeserializationUtil.getBigDecimal(node, "position_after"))
        .sessionRealisedAfter(DeserializationUtil.getBigDecimal(node, "session_realised_after"))
        .positionPnl(DeserializationUtil.getBigDecimal(node, "position_pnl"))
        .perpetualFundingPnl(DeserializationUtil.getBigDecimal(node, "perpetual_funding_pnl"))
        .fee(DeserializationUtil.getBigDecimal(node, "fee"))
        .index(DeserializationUtil.getBigDecimal(node, "index"))
        .feeRate(DeserializationUtil.getBigDecimal(node, "fee_rate"))
        .fundingMark(DeserializationUtil.getBigDecimal(node, "funding_mark"))
        .liquidationFee(DeserializationUtil.getBigDecimal(node, "liquidation_fee"))
        .clientOrderId(DeserializationUtil.getText(node, "client_order_id"))
        .makerTaker(objectMapper.convertValue(node.get("maker_taker"), ThalexMakerTaker.class))
        .rawJson(rawJson)
        .build();
  }
}
