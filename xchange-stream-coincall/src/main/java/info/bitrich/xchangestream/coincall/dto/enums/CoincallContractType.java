package info.bitrich.xchangestream.coincall.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CoincallContractType {
  PERPETUAL("E");

  @JsonValue
  private final String code;

  CoincallContractType(String code) {
    this.code = code;
  }

  @JsonCreator
  public static CoincallContractType fromCode(String code) {
    for (CoincallContractType t : values()) {
      if (t.code.equalsIgnoreCase(code)) {
        return t;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallContractType code: " + code);
  }
}