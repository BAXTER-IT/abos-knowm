package org.knowm.xchange.thalex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ThalexTradeType {
  NORMAL("normal"),
  BLOCK("block"),
  COMBO("combo"),
  AMEND("amend"),
  DELETE("delete"),
  INTERNAL_TRANSFER("internal_transfer"),
  EXPIRATION("expiration"),
  DAILY_MARK("daily_mark"),
  RFQ("rfq"),
  LIQUIDATION("liquidation");

  @JsonValue
  private final String thalexValue;

  @JsonCreator
  public static ThalexTradeType fromThalexValue(String thalexValue) {
    for (ThalexTradeType value : ThalexTradeType.values()) {
      if (value.thalexValue.equals(thalexValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown Thalex trade type value: " + thalexValue);
  }

}
