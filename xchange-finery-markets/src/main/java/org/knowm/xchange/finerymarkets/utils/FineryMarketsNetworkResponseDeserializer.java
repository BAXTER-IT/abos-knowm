package org.knowm.xchange.finerymarkets.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsNetwork;

public class FineryMarketsNetworkResponseDeserializer
    extends JsonDeserializer<FineryMarketsNetwork> {

  @Override
  public FineryMarketsNetwork deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    return FineryMarketsNetwork.builder()
        .name(node.get(0).asText())
        .description(node.get(1).asText())
        .id(node.get(2).asInt())
        .rawData(node.toString())
        .build();
  }
}
