package info.bitrich.xchangestream.coincall.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallMakerTaker {

  MAKER("0"),
  TAKER("1");

  @JsonValue
  private final String code;

  @JsonCreator
  public static CoincallMakerTaker fromCode(String code) {
    if (code == null) {
      throw new IllegalArgumentException("CoincallMakerTaker code cannot be null");
    }
    for (CoincallMakerTaker value : values()) {
      if (value.code.equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallMakerTaker code: " + code);
  }
}