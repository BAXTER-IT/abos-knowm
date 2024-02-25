package org.knowm.xchange.finerymarkets.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsCurrency;

public class FineryMarketsCurrencyResponseDeserializer
    extends JsonDeserializer<FineryMarketsCurrency> {

  @Override
  public FineryMarketsCurrency deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    List<String> networks = new ArrayList<>();
    JsonNode networksNode = node.get(5);
    if (networksNode != null && networksNode.isArray()) {
      for (JsonNode network : networksNode) {
        networks.add(network.asText());
      }
    }
    return FineryMarketsCurrency.builder()
        .name(node.get(0).asText())
        .id(node.get(1).asInt())
        .balanceStep(node.get(2).asInt())
        .price(node.get(3).asLong())
        .typeName(node.get(4).asText())
        .networks(networks)
        .rawData(node.toString())
        .build();
  }
}
