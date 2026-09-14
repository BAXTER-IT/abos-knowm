package org.knowm.xchange.coincall.dtos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallTradeType {

  LIMIT(1),
  MARKET(2),
  POST_ONLY(3),
  STOP_LIMIT(4),
  STOP_MARKET(5),
  BLOCK_TRADE(14);

  @JsonValue
  private final int code;

  @JsonCreator
  public static CoincallTradeType fromCode(int code) {
    for (CoincallTradeType value : CoincallTradeType.values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallTradeType code: " + code);
  }
}