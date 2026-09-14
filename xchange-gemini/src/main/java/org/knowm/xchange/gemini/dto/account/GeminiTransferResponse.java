package org.knowm.xchange.gemini.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiTransferResponse {

  @JsonProperty("type")
  String type;
  @JsonProperty("status")
  String status;
  @JsonProperty("timestampms")
  Long timestampms;
  @JsonProperty("eid")
  String eid;
  @JsonProperty("currency")
  String currency;
  @JsonProperty("amount")
  BigDecimal amount;
  @JsonProperty("method")
  String method;
  @JsonProperty("txHash")
  String txnHash;
  @JsonProperty("outputIdx")
  Long outputIdx;
  @JsonProperty("destination")
  String destination;
  @JsonProperty("purpose")
  String purpose;
}
