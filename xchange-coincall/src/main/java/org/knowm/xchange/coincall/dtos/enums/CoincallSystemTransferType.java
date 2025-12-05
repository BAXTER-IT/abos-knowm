package org.knowm.xchange.coincall.dtos.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoincallSystemTransferType {
  CREDIT(0),
  REWARDS(1),
  TRANSFER(2),
  TRIAL_BONUS(3),
  RELEASE_TRIAL_BONUS(4);

  @JsonValue
  private final int code;

  @JsonCreator
  public static CoincallSystemTransferType fromCode(int code) {
    for (CoincallSystemTransferType value : CoincallSystemTransferType.values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown CoincallSystemTransferType code: " + code);
  }
}