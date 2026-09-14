package org.knowm.xchange.gemini.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeminiInstrumentType {

  SPOT("spot"),
  PERPETUAL("perp");

  private final String exchangeValue;

  @JsonCreator
  public static GeminiInstrumentType fromExchangeValue(String exchangeValue) {
    for (GeminiInstrumentType value : GeminiInstrumentType.values()) {
      if (value.exchangeValue.equals(exchangeValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown Gemini instrument type value: " + exchangeValue);
  }

}
