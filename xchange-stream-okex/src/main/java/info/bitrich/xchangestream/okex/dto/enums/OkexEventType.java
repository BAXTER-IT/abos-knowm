package info.bitrich.xchangestream.okex.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OkexEventType {

  SNAPSHOT("snapshot"),
  EVENT_UPDATE("event_update"),
  DELIVERED("delivered"),
  EXERCISED("exercised"),
  TRANSFERRED("transferred"),
  FILLED("filled"),
  LIQUIDATION("liquidation"),
  CLAW_BACK("claw_back"),
  ADL("adl"),
  FUNDING_FEE("funding_fee"),
  ADJUST_MARGIN("adjust_margin"),
  SET_LEVERAGE("set_leverage"),
  INTEREST_DEDUCTION("interest_deduction"),
  SETTLEMENT("settlement");

  @JsonValue
  private final String code;

  @JsonCreator
  public static OkexEventType fromCode(String code) {
    if (code == null) {
      return null;
    }
    for (OkexEventType value : values()) {
      if (value.code.equalsIgnoreCase(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown OkexEventType: " + code);
  }
}