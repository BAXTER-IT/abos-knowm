package org.knowm.xchange.gemini.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.knowm.xchange.gemini.dto.account.GeminiTransaction;
import org.knowm.xchange.gemini.dto.account.GeminiTransactionTransferResponse;
import org.knowm.xchange.gemini.dto.trade.GeminiTransactionTradeResponse;

public class GeminiTransactionResponseDeserializer extends
    JsonDeserializer<GeminiTransaction> {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public GeminiTransaction deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    String type = node.get("type").asText();

    Class<? extends GeminiTransaction> targetClass;
    switch (type) {
      case "trade":
        targetClass = GeminiTransactionTradeResponse.class;
        break;
      case "transfer":
        targetClass = GeminiTransactionTransferResponse.class;
        break;
      default:
        throw new IOException("Unknown type: " + type);
    }

    return mapper.treeToValue(node, targetClass);
  }
}
