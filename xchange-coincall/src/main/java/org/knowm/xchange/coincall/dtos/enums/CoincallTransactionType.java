package org.knowm.xchange.coincall.dtos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallTransactionType {
  EXTERNAL("external"),
  INTERNAL("internal");

  @JsonValue
  private final String code;

  @JsonCreator
  public static CoincallTransactionType fromCode(String code) {
    if (code == null) {
      return null;
    }
    for (CoincallTransactionType value : CoincallTransactionType.values()) {
      if (value.code.equalsIgnoreCase(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallTransactionType code: " + code);
  }
}