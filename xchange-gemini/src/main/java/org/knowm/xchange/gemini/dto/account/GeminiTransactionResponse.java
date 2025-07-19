package org.knowm.xchange.gemini.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class GeminiTransactionResponse {

  private List<GeminiTransaction> results;

  @JsonProperty("continuation_token")
  private String continuationToken;

}
