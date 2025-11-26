package org.knowm.xchange.bybit.dto.trade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BybitSide {

  BUY("Buy"),
  SELL("Sell"),
  NONE("None"),
  EMPTY("");

  @JsonValue
  private final String value;

  @JsonCreator
  public static BybitSide fromValue(String value) {
    if (value == null) {
      return NONE;
    }

    if (value.isEmpty()) {
      return EMPTY;
    }

    for (BybitSide side : values()) {
      if (side.value.equalsIgnoreCase(value)) {
        return side;
      }
    }

    return null;
  }
}