package org.knowm.xchange.coincall.dtos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallTransactionHistoryQueryType {
  DEPOSIT(0),
  WITHDRAW(1),
  All(-1);

  @JsonValue
  private final int code;

  @JsonCreator
  public static CoincallTransactionHistoryQueryType fromCode(int code) {
    for (CoincallTransactionHistoryQueryType value : CoincallTransactionHistoryQueryType.values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallTransactionHistoryQueryType code: " + code);
  }
}