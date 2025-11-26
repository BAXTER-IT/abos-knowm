package org.knowm.xchange.okex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OkexTriggerPriceType {

  LAST("last"),
  MARK("mark"),
  INDEX("index");

  @JsonValue
  private final String code;

  @JsonCreator
  public static OkexTriggerPriceType fromCode(String code) {
    for (OkexTriggerPriceType value : values()) {
      if (value.code.equalsIgnoreCase(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown OkexTriggerPriceType: " + code);
  }
}
