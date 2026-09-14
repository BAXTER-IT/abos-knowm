package info.bitrich.xchangestream.coincall.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CoincallPositionStatus {
  OPENING(1),
  CLOSED(2);

  @JsonValue
  private final int code;

  CoincallPositionStatus(int code) {
    this.code = code;
  }

  @JsonCreator
  public static CoincallPositionStatus fromCode(String value) {
    int code = Integer.parseInt(value);
    for (CoincallPositionStatus s : values()) {
      if (s.code == code) {
        return s;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallPositionStatus code: " + value);
  }
}