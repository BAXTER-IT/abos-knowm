package org.knowm.xchange.thalex.exceptions;

public class WithdrawalFetchException extends RuntimeException {

  public WithdrawalFetchException(Throwable cause) {
    super("Failed to fetch withdrawals", cause);
  }
}
