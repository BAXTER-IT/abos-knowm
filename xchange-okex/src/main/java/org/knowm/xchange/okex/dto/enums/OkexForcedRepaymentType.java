package org.knowm.xchange.okex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OkexForcedRepaymentType {
  NO_FRP("0"),
  USER_BASED("1"),
  PLATFORM_BASED("2");

  @JsonValue
  private final String code;

  @JsonCreator
  public static OkexForcedRepaymentType fromCode(String code) {
    for (OkexForcedRepaymentType value : values()) {
      if (value.code.equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown OkexForcedRepaymentType code: " + code);
  }
}