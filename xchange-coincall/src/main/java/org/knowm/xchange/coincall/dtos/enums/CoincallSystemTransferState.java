package org.knowm.xchange.coincall.dtos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallSystemTransferState {
  UNSETTLED(0),
  SETTLED(1);

  @JsonValue
  private final int code;

  @JsonCreator
  public static CoincallSystemTransferState fromCode(int code) {
    for (CoincallSystemTransferState value : CoincallSystemTransferState.values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallSystemTransferState code: " + code);
  }
}