package org.knowm.xchange.thalex.exceptions;

public class TransfersFetchException extends RuntimeException {

  public TransfersFetchException(Throwable cause) {
    super("Failed to fetch transfers", cause);
  }
}
