package org.knowm.xchange.gemini.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.knowm.xchange.gemini.dto.GeminiPostRequest;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GeminiPastTradesRequest extends GeminiPostRequest {

  private final String request = "/v1/mytrades";

  private String symbol;

  private Long timestamp;

  @Default
  @JsonProperty("limit_trades")
  private Integer limit = 500;

  @Override
  public String getRequest() {
    return request;
  }

  @Override
  public String toString() {
    return "GeminiPastTradesRequest{" +
        "request='" + request + '\'' +
        ", symbol='" + symbol + '\'' +
        ", timestamp=" + timestamp +
        ", limit=" + limit +
        ", nonce=" + nonce +
        ", account='" + account + '\'' +
        '}';
  }
}
