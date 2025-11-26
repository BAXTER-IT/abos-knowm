package org.knowm.xchange.okex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OkexMarginMode {

  CROSS("cross"),
  ISOLATED("isolated");

  @JsonValue
  private final String code;

  @JsonCreator
  public static OkexMarginMode fromCode(String code) {
    for (OkexMarginMode value : values()) {
      if (value.code.equalsIgnoreCase(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown OkexMarginMode: " + code);
  }
}