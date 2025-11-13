package info.bitrich.xchangestream.coincall.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CoincallPositionSide {
  LONG(1),
  SHORT(2);

  @JsonValue
  private final int code;

  CoincallPositionSide(int code) {
    this.code = code;
  }

  @JsonCreator
  public static CoincallPositionSide fromCode(String value) {
    int code = Integer.parseInt(value);
    for (CoincallPositionSide s : values()) {
      if (s.code == code) {
        return s;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallPositionSide code: " + value);
  }
}