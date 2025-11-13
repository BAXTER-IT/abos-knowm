package org.knowm.xchange.coincall.dtos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallMarginMode {
  SINGLE(1),
  PORTFOLIO(2),
  MULTICURRENCY(3);

  @JsonValue
  private final int code;

  @JsonCreator
  public static CoincallMarginMode fromCode(int code) {
    for (CoincallMarginMode value : CoincallMarginMode.values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallMarginMode code: " + code);
  }
}