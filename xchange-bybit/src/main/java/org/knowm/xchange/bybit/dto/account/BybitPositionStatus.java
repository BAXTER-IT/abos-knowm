package org.knowm.xchange.bybit.dto.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BybitPositionStatus {

  NORMAL("Normal"),
  LIQUIDATION("Liq"),
  ADL("Adl");

  @JsonValue
  private final String value;

  @JsonCreator
  public static BybitPositionStatus fromCode(String value) {
    for (BybitPositionStatus status : values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    return null;
  }
}