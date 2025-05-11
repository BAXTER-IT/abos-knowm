package org.knowm.xchange.thalex.exceptions;

public class AssetFetchException extends RuntimeException {

  public AssetFetchException(Throwable cause) {
    super("Failed to fetch assets", cause);
  }
}
