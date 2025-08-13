package org.knowm.xchange.thalex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ThalexWithdrawalState {
  PENDING("pending"),
  AWAITING_CONFIRMATION("awaiting_confirmation"),
  EXECUTING("executing"),
  EXECUTED("executed"),
  REJECTED("rejected");

  @JsonValue
  private final String thalexValue;

  @JsonCreator
  public static ThalexWithdrawalState fromThalexValue(String thalexValue) {
    for (ThalexWithdrawalState value : ThalexWithdrawalState.values()) {
      if (value.thalexValue.equals(thalexValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown withdrawal state: " + thalexValue);
  }
}
