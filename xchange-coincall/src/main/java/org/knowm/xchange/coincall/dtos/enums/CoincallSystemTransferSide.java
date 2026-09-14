package org.knowm.xchange.coincall.dtos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallSystemTransferSide {
  INCREASE(0),
  DECREASE(1);

  @JsonValue
  private final int code;

  @JsonCreator
  public static CoincallSystemTransferSide fromCode(int code) {
    for (CoincallSystemTransferSide value : CoincallSystemTransferSide.values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallSystemTransferSide code: " + code);
  }
}