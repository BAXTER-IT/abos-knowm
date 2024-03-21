package org.knowm.xchange.enums;

public enum HashingAlgorithm {
  SHA256("HmacSHA256"),
  SHA384("HmacSHA384"),
  SHA512("HmacSHA512");

  private final String algorithm;

  HashingAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  @Override
  public String toString() {
    return algorithm;
  }
}
