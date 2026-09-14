package org.knowm.xchange.gemini.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeminiTradeBreak {

  TRADE_CORRECT("trade correct"),
  MANUAL("manual"),
  FULL("full");

  private final String exchangeValue;

  @JsonCreator
  public static GeminiTradeBreak fromExchangeValue(String exchangeValue) {
    for (GeminiTradeBreak value : GeminiTradeBreak.values()) {
      if (value.exchangeValue.equals(exchangeValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown Gemini trade break value: " + exchangeValue);
  }
}