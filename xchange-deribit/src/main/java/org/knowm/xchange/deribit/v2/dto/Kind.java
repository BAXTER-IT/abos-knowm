package org.knowm.xchange.deribit.v2.dto;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Kind {
  @JsonProperty("future")
  FUTURES("future"),

  @JsonProperty("option")
  OPTIONS("option"),

  @JsonProperty("spot")
  SPOT("spot"),

  @JsonProperty("future_combo")
  FUTURES_COMBO("future_combo"),

  @JsonProperty("option_combo")
  OPTIONS_COMBO("option_combo"),

  @JsonEnumDefaultValue
  UNKNOWN("unknown");

  private final String value;

  Kind(String value) {
    this.value = value;
  }

  /**
   * The value Deribit expects on the wire.
   *
   * <p>This enum travels two different paths and only one of them is Jackson's. As a response field
   * it is bound by Jackson, which honours the {@code @JsonProperty} above. As a {@code @QueryParam}
   * — see {@code DeribitAuthenticated#getUserTradesByCurrencyAndTime} and {@code Deribit} — it is
   * rendered by rescu, which falls through to {@code String.valueOf(Object)} and so takes whatever
   * {@code toString()} returns; the Jackson annotations are invisible to it. Without this override
   * the constant name went out as {@code kind=OPTIONS} and Deribit rejected the request with
   * {@code -32602: Invalid params, {reason=invalid value, param=kind}}.
   */
  @Override
  public String toString() {
    return value;
  }
}
