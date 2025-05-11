package org.knowm.xchange.thalex.exceptions;

public class LedgerFetchException extends RuntimeException {

  public LedgerFetchException(Throwable cause) {
    super("Failed to fetch ledger", cause);
  }
}
