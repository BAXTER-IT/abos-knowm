package org.knowm.xchange.coincall.dtos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallTradeSide {
  BUY(1),
  SELL(2);

  @JsonValue
  private final int code;

  @JsonCreator
  public static CoincallTradeSide fromCode(Object rawCode) {
    int code = Integer.parseInt(rawCode.toString());
    for (CoincallTradeSide value : CoincallTradeSide.values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallTradeSide code: " + code);
  }
}