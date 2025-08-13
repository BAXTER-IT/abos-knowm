package org.knowm.xchange.thalex.exceptions;

public class BalanceFetchException extends RuntimeException {

  public BalanceFetchException(Throwable cause) {
    super("Failed to fetch balance", cause);
  }
}
