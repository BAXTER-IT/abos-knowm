package org.knowm.xchange.thalex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ThalexDepositStatus {
  UNCONFIRMED("unconfirmed"),
  CONFIRMED("confirmed");

  @JsonValue
  private final String thalexValue;

  @JsonCreator
  public static ThalexDepositStatus fromThalexValue(String thalexValue) {
    for (ThalexDepositStatus value : ThalexDepositStatus.values()) {
      if (value.thalexValue.equals(thalexValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown Thalex value: " + thalexValue);
  }
}
