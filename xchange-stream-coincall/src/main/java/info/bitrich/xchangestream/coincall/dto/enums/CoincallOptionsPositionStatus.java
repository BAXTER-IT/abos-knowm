package info.bitrich.xchangestream.coincall.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CoincallOptionsPositionStatus {
  OPENING(1),
  CLOSED(5);

  @JsonValue
  private final int code;

  CoincallOptionsPositionStatus(int code) {
    this.code = code;
  }

  @JsonCreator
  public static CoincallOptionsPositionStatus fromCode(String value) {
    int code = Integer.parseInt(value);
    for (CoincallOptionsPositionStatus s : values()) {
      if (s.code == code) {
        return s;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallOptionsPositionStatus code: " + value);
  }
}