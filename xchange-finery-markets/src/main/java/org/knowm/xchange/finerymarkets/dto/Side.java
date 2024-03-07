package org.knowm.xchange.finerymarkets.dto;

public enum Side {
  BID(0),
  ASK(1);

  private final int apiIndex;

  Side(int apiIndex) {
    this.apiIndex = apiIndex;
  }

  public static Side fromApiIndex(int index) {
    for (Side side : values()) {
      if (side.apiIndex == index) {
        return side;
      }
    }
    return null;
  }
}
