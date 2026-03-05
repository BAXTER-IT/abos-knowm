package org.knowm.xchange.okex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OkexAutoLendStatus {
  UNSUPPORTED("unsupported"),
  OFF("off"),
  PENDING("pending"),
  ACTIVE("active");

  @JsonValue
  private final String code;

  @JsonCreator
  public static OkexAutoLendStatus fromCode(String code) {
    for (OkexAutoLendStatus value : values()) {
      if (value.code.equalsIgnoreCase(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown OkexAutoLendStatus code: " + code);
  }
}