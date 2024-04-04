package org.knowm.xchange.finerymarkets.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.knowm.xchange.finerymarkets.dto.trade.DealHistory;
import org.knowm.xchange.finerymarkets.streaming.dtos.FineryMarketsStreamingOrderResponseDto;
import org.knowm.xchange.finerymarkets.streaming.enums.Action;
import org.knowm.xchange.finerymarkets.streaming.enums.Feed;
import org.knowm.xchange.finerymarkets.streaming.exceptions.FailedToSubscribeException;

public class FineryMarketsStreamingOrderResponseDeserializer
    extends JsonDeserializer<FineryMarketsStreamingOrderResponseDto> {

  @Override
  public FineryMarketsStreamingOrderResponseDto deserialize(
      JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);

    Feed feed = Feed.fromString(node.get(0).asText());
    Action action = Action.fromString(node.get(2).asText());
    if (action == Action.FAILED_SUBSCRIBE) {
      throw new FailedToSubscribeException(feed);
    }

    List<DealHistory> deals = new ArrayList<>();
    if (node.get(3).get(0).isArray()) {
      for (JsonNode dealNode : node.get(3)) {
        deals.add(p.getCodec().treeToValue(dealNode, DealHistory.class));
      }
    } else {
      deals = Collections.singletonList(p.getCodec().treeToValue(node.get(3), DealHistory.class));
    }
    return FineryMarketsStreamingOrderResponseDto.builder()
        .feed(feed)
        .feedId(node.get(1).asText())
        .action(action)
        .deals(deals)
        .build();
  }
}
