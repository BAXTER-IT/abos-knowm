package org.knowm.xchange.bybit.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BybitCategory {
  SPOT("spot"),
  LINEAR("linear"),
  INVERSE("inverse"),
  OPTION("option");

  @JsonValue
  private final String value;

  @JsonCreator
  public static BybitCategory fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (BybitCategory c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    return null;
  }
}