package org.knowm.xchange.okex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OkexDeltaNeutralStatus {
  NORMAL("0"),
  TRANSFER_RESTRICTED("1"),
  DELTA_REDUCING("2");

  @JsonValue
  private final String code;

  @JsonCreator
  public static OkexDeltaNeutralStatus fromCode(String code) {
    for (OkexDeltaNeutralStatus value : values()) {
      if (value.code.equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown OkexDeltaNeutralStatus code: " + code);
  }
}