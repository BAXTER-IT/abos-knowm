package org.knowm.xchange.okex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OkexCollateralRestrictionStatus {
  NOT_RESTRICTED("0"),
  NEAR_LIMIT("1"),
  RESTRICTED("2");

  @JsonValue
  private final String code;

  @JsonCreator
  public static OkexCollateralRestrictionStatus fromCode(String code) {
    for (OkexCollateralRestrictionStatus value : values()) {
      if (value.code.equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown OkexCollateralRestrictionStatus code: " + code);
  }
}