package org.knowm.xchange.bitget.config.deserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import org.knowm.xchange.bitget.config.converter.StringToCurrencyConverter;
import org.knowm.xchange.bitget.config.converter.StringToFundingRecordStatusConverter;
import org.knowm.xchange.bitget.dto.account.BitgetAccountType;
import org.knowm.xchange.bitget.dto.account.BitgetTransferRecordDto;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.FundingRecord.Status;

public class BitgetTransferRecordDtoDeserializer extends JsonDeserializer<BitgetTransferRecordDto> {

  @Override
  public BitgetTransferRecordDto deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);

    String currencyString = node.get("coin").asText();
    StringToCurrencyConverter converter = new StringToCurrencyConverter();
    Currency currency = converter.convert(currencyString);

    String statusString = node.get("status").asText();
    StringToFundingRecordStatusConverter statusConverter = new StringToFundingRecordStatusConverter();
    Status status = statusConverter.convert(statusString);

    String toAccountTypeString = node.get("toType").asText();
    BitgetAccountType toAccountType = BitgetAccountType.valueOf(toAccountTypeString.toUpperCase());

    String fromAccountTypeString = node.get("fromType").asText();
    BitgetAccountType fromAccountType = BitgetAccountType.valueOf(fromAccountTypeString.toUpperCase());

    return BitgetTransferRecordDto.builder()
        .clientOid(node.get("clientOid").asText())
        .transferId(node.get("transferId").asText())
        .currency(currency)
        .status(status)
        .toAccountType(toAccountType)
        .toSymbol(node.get("toSymbol").asText())
        .fromAccountType(fromAccountType)
        .fromSymbol(node.get("fromSymbol").asText())
        .size(node.get("size").decimalValue())
        .timestamp(Instant.ofEpochMilli(node.get("ts").asLong()))
        .rawJson(node.toString())
        .build();
  }
}
