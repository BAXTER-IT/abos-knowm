package org.knowm.xchange.finerymarkets.streaming.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum Event {
  SUBSCRIBE("bind"),
  UNSUBSCRIBE("unbind"),
  PING("ping");

  private final String value;

  Event(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
