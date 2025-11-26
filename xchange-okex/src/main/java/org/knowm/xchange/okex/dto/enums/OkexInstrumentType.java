package org.knowm.xchange.okex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OkexInstrumentType {

  SPOT("SPOT"),
  MARGIN("MARGIN"),
  SWAP("SWAP"),
  FUTURES("FUTURES"),
  OPTION("OPTION"),
  ANY("ANY");

  @JsonValue
  private final String code;

  @JsonCreator
  public static OkexInstrumentType fromCode(String code) {
    for (OkexInstrumentType value : values()) {
      if (value.code.equalsIgnoreCase(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown OkexInstrumentType: " + code);
  }
}