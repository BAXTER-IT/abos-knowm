package info.bitrich.xchangestream.kucoin.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KucoinMarginMode {
  CROSS("CROSS"),
  ISOLATED("ISOLATED");

  @JsonValue
  private final String code;

  @JsonCreator
  public static KucoinMarginMode fromCode(String code) {
    if (code == null) return null;
    for (KucoinMarginMode v : values()) {
      if (v.code.equalsIgnoreCase(code)) return v;
    }
    throw new IllegalArgumentException("Unknown KucoinMarginMode: " + code);
  }
}