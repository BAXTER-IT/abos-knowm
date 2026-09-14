package org.knowm.xchange.gemini.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.knowm.xchange.gemini.dto.GeminiPostRequest;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GeminiTransactionRequest extends GeminiPostRequest {

  private final String request = "/v1/transactions";

  @JsonProperty("timestamp_nanos")
  private Long timestampNanos;

  @Default
  private Integer limit = 300;

  @JsonProperty("continuation_token")
  private String continuationToken;

  @Override
  public String getRequest() {
    return request;
  }

  @Override
  public String toString() {
    return "GeminiTransactionRequest{" +
        "request='" + request + '\'' +
        ", timestampNanos=" + timestampNanos +
        ", limit=" + limit +
        ", continuationToken='" + continuationToken + '\'' +
        ", nonce=" + nonce +
        ", account='" + account + '\'' +
        '}';
  }
}
