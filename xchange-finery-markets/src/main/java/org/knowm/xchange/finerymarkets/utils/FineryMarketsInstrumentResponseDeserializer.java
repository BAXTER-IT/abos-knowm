package org.knowm.xchange.finerymarkets.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsInstrument;

public class FineryMarketsInstrumentResponseDeserializer
    extends JsonDeserializer<FineryMarketsInstrument> {

  @Override
  public FineryMarketsInstrument deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    return FineryMarketsInstrument.builder()
        .name(node.get(0).asText())
        .id(node.get(1).asLong())
        .assetCurrencyName(node.get(2).asText())
        .balanceCurrencyName(node.get(3).asText())
        .rawData(node.toString())
        .build();
  }
}
