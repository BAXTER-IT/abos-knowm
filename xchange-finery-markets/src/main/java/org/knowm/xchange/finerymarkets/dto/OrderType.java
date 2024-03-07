package org.knowm.xchange.finerymarkets.dto;

public enum OrderType {
  LIMIT(0),
  POST_ONLY(1),
  LIMIT_IOC(2),
  LIMIT_FOK(3),
  MARKET_IOC(4),
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
