package org.knowm.xchange.coincall.dtos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallUserType {
  MAIN(1),
  SUBACCOUNT(2);

  @JsonValue
  private final int code;

  @JsonCreator
  public static CoincallUserType fromCode(int code) {
    for (CoincallUserType value : CoincallUserType.values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallUserType code: " + code);
  }
}