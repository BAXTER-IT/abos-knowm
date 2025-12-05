package org.knowm.xchange.coincall.dtos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallTransactionStatus {
  CANCEL("cancel"),
  PROCESSING("processing"),
  CANCELLED("cancelled"),
  COMPLETED("completed"),
  FAILED("failed");

  @JsonValue
  private final String code;

  @JsonCreator
  public static CoincallTransactionStatus fromCode(String code) {
    if (code == null) {
      return null;
    }
    for (CoincallTransactionStatus value : CoincallTransactionStatus.values()) {
      if (value.code.equalsIgnoreCase(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallTransactionStatus code: " + code);
  }
}