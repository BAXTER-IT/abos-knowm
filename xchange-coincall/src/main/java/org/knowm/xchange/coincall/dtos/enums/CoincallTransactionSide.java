package org.knowm.xchange.coincall.dtos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallTransactionSide {
  DEPOSIT("deposit"),
  WITHDRAW("withdraw");

  @JsonValue
  private final String code;

  @JsonCreator
  public static CoincallTransactionSide fromCode(String code) {
    for (CoincallTransactionSide value : CoincallTransactionSide.values()) {
      if (value.code.equalsIgnoreCase(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallTransactionSide code: " + code);
  }
}