package info.bitrich.xchangestream.gemini.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeminiWsTradeSide {

  BUY("buy"),
  SELL("sell");

  private final String geminiValue;

  @JsonCreator
  public static GeminiWsTradeSide fromExchangeValue(String exchangeValue) {
    for (GeminiWsTradeSide value : GeminiWsTradeSide.values()) {
      if (value.geminiValue.equalsIgnoreCase(exchangeValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown Gemini trade side value: " + exchangeValue);
  }
}