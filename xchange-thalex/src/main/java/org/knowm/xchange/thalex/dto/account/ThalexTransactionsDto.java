package org.knowm.xchange.thalex.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class ThalexTransactionsDto {

  @JsonProperty("transactions")
  private List<ThalexTransactionDto> transactions;

  @JsonProperty("bookmark")
  private String bookmark;
}
