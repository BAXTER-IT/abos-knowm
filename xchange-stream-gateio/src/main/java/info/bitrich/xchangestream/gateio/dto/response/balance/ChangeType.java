package info.bitrich.xchangestream.gateio.dto.response.balance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonIgnoreProperties(ignoreUnknown = true)
public enum ChangeType {

  WITHDRAW("withdraw"),
  DEPOSIT("deposit"),
  TRADE_FEE_DEDUCT("trade-fee-deduct"),
  ORDER_CREATE("order-create"),
  ORDER_MATCH("order-match"),
  ORDER_UPDATE("order-update"),
  MARGIN_TRANSFER("margin-transfer"),
  FUTURE_TRANSFER("future-transfer"),
  CROSS_MARGIN_TRANSFER("cross-margin-transfer"),
  REFERRAL_FEE("referral-fee"),
  SUB_TRANSFER("sub-transfer"),
  SPOT_TRANSFER("spot-transfer"),
  FEE("fee"),
  OTHER("other");

  @JsonValue
  private final String code;

  ChangeType(String code) {
    this.code = code;
  }

  @JsonCreator
  public static ChangeType fromCode(String code) {
    if (code == null) {
      return OTHER;
    }
    for (ChangeType t : values()) {
      if (t.code.equalsIgnoreCase(code)) {
        return t;
      }
    }
    return OTHER; // spec says “other” exists
  }
}
