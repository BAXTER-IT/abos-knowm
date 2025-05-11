package org.knowm.xchange.thalex.exceptions;

public class DepositFetchException extends RuntimeException {

  public DepositFetchException(Throwable cause) {
    super("Failed to fetch deposits", cause);
  }
}
