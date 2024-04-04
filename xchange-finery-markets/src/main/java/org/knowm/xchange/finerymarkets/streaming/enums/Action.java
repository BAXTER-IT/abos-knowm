package org.knowm.xchange.finerymarkets.streaming.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


public enum Action {
  SNAPSHOT("S"),
  NEW_ORDER("+"),
  DEL_ORDER("-"),
  NEW_DEAL("D"),
  FAILED_SUBSCRIBE("Z"),
  UNSUBSCRIBED("U");

  private final String value;

  Action(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  public static Action fromString(String value) {
    for (Action action : Action.values()) {
      if (action.value.equals(value)) {
        return action;
      }
    }
    throw new IllegalArgumentException("Unknown Action: " + value);
  }
}
