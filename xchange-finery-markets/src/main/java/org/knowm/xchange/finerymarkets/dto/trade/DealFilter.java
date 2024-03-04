package org.knowm.xchange.finerymarkets.dto.trade;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DealFilter {
  ALL("all"),
  SUBACCOUNTS("subaccounts"),
  EXTERNAL("external");

  private final String value;

  DealFilter(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
