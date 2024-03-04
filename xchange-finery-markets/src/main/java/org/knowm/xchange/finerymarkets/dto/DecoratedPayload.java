package org.knowm.xchange.finerymarkets.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class DecoratedPayload {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  @JsonValue private final Map<String, Object> data;

  public DecoratedPayload(FineryMarketsRequest data) {
    this.data = convert(data);
  }

  public DecoratedPayload() {
    this.data = new HashMap<>();
  }

  /** Set nonce and timestamp to the same value based on current time */
  public void setNonceAndTimestamp() {
    setNonceAndTimestamp(getCurrentTime());
  }

  /**
   * Set nonce and timestamp to the same value based on the timestamp provided
   *
   * @param timestamp in milliseconds
   */
  void setNonceAndTimestamp(long timestamp) {
    setNonceAndTimestamp(timestamp, timestamp);
  }

  /**
   * Set nonce and timestamp to the provided values
   *
   * @param nonce ever-increasing number
   * @param timestamp in milliseconds
   */
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

  public String getPayloadString() {
    try {
      return objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      log.error("Error converting payload to string", e);
      return "";
    }
  }

  @Override
  public String toString() {
    return "FineryMarketsDecoratedPayload{" + "data=" + data + '}';
  }
}
