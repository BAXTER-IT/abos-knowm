package org.knowm.xchange.thalex.config.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.thalex.dto.account.ThalexTransactionDto;
import org.knowm.xchange.thalex.dto.enums.ThalexTransactionType;
import org.knowm.xchange.thalex.utils.DeserializationUtil;

public class ThalexTransactionDtoDeserializer extends JsonDeserializer<ThalexTransactionDto> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public ThalexTransactionDto deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    String rawJson = node.toString();

    return ThalexTransactionDto.builder()
        .asset(objectMapper.convertValue(node.get("asset"), Currency.class))
        .time(DeserializationUtil.getInstant(node))
        .amount(DeserializationUtil.getBigDecimal(node, "amount"))
        .instrumentName(DeserializationUtil.getText(node, "instrument_name"))
        .transactionType(getTransactionType(node))
        .description(DeserializationUtil.getText(node, "description"))
        .balanceAfter(DeserializationUtil.getBigDecimal(node, "balance_after"))
        .rawJson(rawJson)
        .build();
  }

  private static ThalexTransactionType getTransactionType(JsonNode node) {
    return ThalexTransactionType.fromThalexValue(
        DeserializationUtil.getText(node, "transaction_type"));
  }
}
