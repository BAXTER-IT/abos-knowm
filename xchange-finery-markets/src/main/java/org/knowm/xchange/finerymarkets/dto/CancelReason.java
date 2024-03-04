package org.knowm.xchange.finerymarkets.dto;

public enum CancelReason {
  IN_PLACE_OR_FILLED,
  BY_CLIENT,
  AS_NON_BOOK_ORDER,
  BY_SELF_TRADE_PREVENTION,
  BY_CANCEL_ON_DISCONNECT
}
