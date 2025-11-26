package org.knowm.xchange.okex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OkexPositionSide {

  LONG("long"),
  SHORT("short"),
  NET("net");

  @JsonValue
  private final String code;

  @JsonCreator
  public static OkexPositionSide fromCode(String code) {
    for (OkexPositionSide value : values()) {
      if (value.code.equalsIgnoreCase(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown OkexPositionSide: " + code);
  }
}