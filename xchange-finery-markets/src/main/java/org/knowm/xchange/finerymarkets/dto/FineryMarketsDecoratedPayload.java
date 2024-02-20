package org.knowm.xchange.finerymarkets.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.finerymarkets.dto.marketdata.request.FineryMarketsRequest;

@Slf4j
public class FineryMarketsDecoratedPayload implements FineryMarketsPayload {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final Map<String, Object> data;

  public FineryMarketsDecoratedPayload(FineryMarketsRequest data) {
    this.data = convert(data);
  }

  public FineryMarketsDecoratedPayload() {
    this.data = new HashMap<>();
  }

  /**
   * Set nonce and timestamp to the same value based on the timestamp provided
   *
   * @param timestamp in milliseconds
   */
  void setNonceAndTimestamp(long timestamp) {
    setNonceAndTimestamp(timestamp, timestamp);
  }

  void setNonceAndTimestamp(long nonce, long timestamp) {
    data.put("nonce", nonce);
    data.put("timestamp", timestamp);
  }

  Map<String, Object> convert(FineryMarketsRequest input) {
    JavaType type =
        objectMapper
            .getTypeFactory()
            .constructType(new TypeReference<HashMap<String, Object>>() {});
    return objectMapper.convertValue(input, type);
  }

  long getCurrentTime() {
    return System.currentTimeMillis();
  }

  @Override
  public String getPayloadString() {
    setNonceAndTimestamp(getCurrentTime());
    try {
      return objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      log.error("Error converting payload to string", e);
      return "";
    }
  }
}
