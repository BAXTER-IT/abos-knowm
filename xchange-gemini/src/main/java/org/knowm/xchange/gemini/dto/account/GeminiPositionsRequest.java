package org.knowm.xchange.gemini.dto.account;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.knowm.xchange.gemini.dto.GeminiPostRequest;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GeminiPositionsRequest extends GeminiPostRequest {

  private final String request = "/v1/positions";

  @Override
  public String getRequest() {
    return request;
  }

  @Override
  public String toString() {
    return "GeminiPositionsRequest{" +
        "request='" + request + '\'' +
        ", nonce=" + nonce +
        ", account='" + account + '\'' +
        '}';
  }
}
