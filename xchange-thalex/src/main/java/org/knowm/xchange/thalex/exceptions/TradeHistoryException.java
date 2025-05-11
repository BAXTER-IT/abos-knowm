package org.knowm.xchange.thalex.exceptions;

public class TradeHistoryException extends RuntimeException {

  public TradeHistoryException(Throwable cause) {
    super("Failed to fetch trade history", cause);
  }
}
