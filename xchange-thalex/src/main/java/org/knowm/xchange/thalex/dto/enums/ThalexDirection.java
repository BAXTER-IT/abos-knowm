package org.knowm.xchange.thalex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ThalexDirection {
  BUY("buy"),
  SELL("sell");

  private final String thalexValue;

  @JsonCreator
  public static ThalexDirection fromThalexValue(String thalexValue) {
    for (ThalexDirection value : ThalexDirection.values()) {
      if (value.thalexValue.equals(thalexValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown Thalex direction value: " + thalexValue);
  }
}
