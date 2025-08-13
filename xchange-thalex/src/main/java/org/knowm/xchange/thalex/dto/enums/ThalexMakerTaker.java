package org.knowm.xchange.thalex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ThalexMakerTaker {
  MAKER("maker"),
  TAKER("taker");

  @JsonValue
  private final String thalexValue;

  @JsonCreator
  public static ThalexMakerTaker fromThalexValue(String thalexValue) {
    for (ThalexMakerTaker value : ThalexMakerTaker.values()) {
      if (value.thalexValue.equals(thalexValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown Thalex makerTaker value: " + thalexValue);
  }

}
