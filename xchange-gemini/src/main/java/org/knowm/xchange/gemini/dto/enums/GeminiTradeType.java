package org.knowm.xchange.gemini.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeminiTradeType {

  BUY("Buy"),
  SELL("Sell");

  private final String geminiValue;

  @JsonCreator
  public static GeminiTradeType fromExchangeValue(String exchangeValue) {
    for (GeminiTradeType value : GeminiTradeType.values()) {
      if (value.geminiValue.equals(exchangeValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown Gemini trade type value: " + exchangeValue);
  }
}
