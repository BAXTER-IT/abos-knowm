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
public class CoincallWsDerivativeResponseDto<T> {

  @JsonProperty("dt")
  String dataType;

  @JsonProperty("c")
  Integer code;

  @JsonProperty("rc")
  Integer responseCode;

  @JsonProperty("d")
  T data;

  @JsonProperty("ts")
  Long timestamp;
}