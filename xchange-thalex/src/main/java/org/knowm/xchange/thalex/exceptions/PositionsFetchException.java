package org.knowm.xchange.thalex.exceptions;

public class PositionsFetchException extends RuntimeException {

  public PositionsFetchException(Throwable cause) {
    super("Failed to fetch positions", cause);
  }
}
