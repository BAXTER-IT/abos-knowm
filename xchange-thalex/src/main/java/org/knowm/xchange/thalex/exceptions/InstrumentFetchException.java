package org.knowm.xchange.thalex.exceptions;

public class InstrumentFetchException extends RuntimeException {

  public InstrumentFetchException(Throwable cause) {
    super("Failed to fetch instruments", cause);
  }
}
