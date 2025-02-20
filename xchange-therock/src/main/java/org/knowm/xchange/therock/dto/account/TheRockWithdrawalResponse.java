package org.knowm.xchange.therock.dto.account;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TheRockWithdrawalResponse {

  private Integer transactionId;

  public Integer getTransactionId() {
    return transactionId;
  }

  @Override
  public String toString() {
    return String.format("TheRockWithdrawalResponse{transactionId=%d}", transactionId);
  }
}
