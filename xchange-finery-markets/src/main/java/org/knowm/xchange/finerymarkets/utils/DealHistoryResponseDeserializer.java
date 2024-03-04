package org.knowm.xchange.finerymarkets.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.finerymarkets.dto.trade.DealHistory;
import org.knowm.xchange.finerymarkets.dto.trade.response.DealHistoryResponse;

public class DealHistoryResponseDeserializer extends JsonDeserializer<DealHistoryResponse> {

  @Override
  public DealHistoryResponse deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    DealHistoryResponse output = new DealHistoryResponse();
    List<DealHistory> dealHistories = new ArrayList<>();
    JsonNode node = p.getCodec().readTree(p);
    for (JsonNode entry : node) {
      dealHistories.add(mapper.treeToValue(entry, DealHistory.class));
    }
    output.setDeals(dealHistories);
    return output;
  }
}
