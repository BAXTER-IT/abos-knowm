package org.knowm.xchange.finerymarkets.dto;

public enum OrderType {
  LIMIT(0),
  POST_ONLY(1),
  /** Immediate-Or-Cancel */
  LIMIT_IOC(2),
  /** Fill-Or-Kill */
  LIMIT_FOK(3),
  /** Immediate-Or-Cancel */
  MARKET_IOC(4),
  /** Fill-Or-Kill */
  MARKET_FOK(5),
  MANUAL_TRADE(6),
  LIQUIDATION_TRADE(7);

  private final int apiIndex;

  OrderType(int apiIndex) {
    this.apiIndex = apiIndex;
  }

  public static OrderType fromApiIndex(int index) {
    for (OrderType orderType : values()) {
      if (orderType.apiIndex == index) {
        return orderType;
      }
    }
    return null;
  }
}
