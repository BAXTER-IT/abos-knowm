package org.knowm.xchange.finerymarkets.dto;

public enum CancelReason {
  IN_PLACE_OR_FILLED(0),
  BY_CLIENT(1),
  AS_NON_BOOK_ORDER(2),
  BY_SELF_TRADE_PREVENTION(3),
  BY_CANCEL_ON_DISCONNECT(4);

  private final int apiIndex;

  CancelReason(int apiIndex) {
    this.apiIndex = apiIndex;
  }

  public static CancelReason fromApiIndex(int index) {
    for (CancelReason cancelReason : values()) {
      if (cancelReason.apiIndex == index) {
        return cancelReason;
      }
    }
    return null;
  }
}
