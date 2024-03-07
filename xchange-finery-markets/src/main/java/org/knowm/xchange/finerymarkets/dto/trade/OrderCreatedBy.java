package org.knowm.xchange.finerymarkets.dto.trade;

public enum OrderCreatedBy {
  SIZE(0),
  VOLUME(1);

  private final int apiIndex;

  OrderCreatedBy(int apiIndex) {
    this.apiIndex = apiIndex;
  }

  public static OrderCreatedBy fromApiIndex(int index) {
    for (OrderCreatedBy orderCreatedBy : values()) {
      if (orderCreatedBy.apiIndex == index) {
        return orderCreatedBy;
      }
    }
    return null;
  }
}
