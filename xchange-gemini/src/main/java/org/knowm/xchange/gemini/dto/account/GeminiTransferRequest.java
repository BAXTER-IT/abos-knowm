package org.knowm.xchange.gemini.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.knowm.xchange.gemini.dto.GeminiPostRequest;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GeminiTransferRequest extends GeminiPostRequest {

  private final String request = "/v1/transfers";

  private Long timestamp;

  @Default
  @JsonProperty("limit_transfers")
  private Integer limit = 50;

  /**
   * Uppercase, e.g. BTC
   */
  private String currency;

  @JsonProperty("show_completed_deposit_advances")
  private Boolean showCompletedDepositAdvances;

  @Override
  public String getRequest() {
    return request;
  }

  @Override
  public String toString() {
    return "GeminiTransferRequest{" +
        "request='" + request + '\'' +
        ", timestamp=" + timestamp +
        ", limit=" + limit +
        ", currency='" + currency + '\'' +
        ", showCompletedDepositAdvances=" + showCompletedDepositAdvances +
        ", nonce=" + nonce +
        ", account='" + account + '\'' +
        '}';
  }
}
