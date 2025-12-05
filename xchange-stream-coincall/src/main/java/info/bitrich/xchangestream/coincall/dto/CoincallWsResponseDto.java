package info.bitrich.xchangestream.coincall.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoincallWsResponseDto<T> {

  /**
   * Channel name, e.g. "trade", "order", "position".
   */
  @JsonProperty("ch")
  String channel;

  /**
   * Server timestamp (ms).
   */
  @JsonProperty("ts")
  Long timestamp;

  /**
   * Response status: usually "ok".
   */
  @JsonProperty("status")
  String status;

  /**
   * Tick payload containing the actual event data.
   */
  @JsonProperty("tick")
  T data;
}