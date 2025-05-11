package org.knowm.xchange.thalex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ThalexInstrumentType {
  PERPETUAL("perpetual"),
  FUTURE("future"),
  OPTION("option"),
  COMBINATION("combination");

  @JsonValue
  private final String thalexValue;

  @JsonCreator
  public static ThalexInstrumentType fromThalexValue(String thalexValue) {
    for (ThalexInstrumentType value : ThalexInstrumentType.values()) {
      if (value.thalexValue.equals(thalexValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException(
        "Unknown Thalex instrument type thalexValue: " + thalexValue);
  }
}
