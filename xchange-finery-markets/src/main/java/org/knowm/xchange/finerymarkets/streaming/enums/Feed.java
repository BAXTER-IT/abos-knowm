package org.knowm.xchange.finerymarkets.streaming.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum Feed {
  /** Currencies and Instruments feed */
  INSTRUMENTS("I"),
  /** Positions, Orders and Settlement Orders feed */
  POSITIONS_ORDERS_SETTLEMENTS("P"),
  GLOBAL_LIMITS("G"),
  COUNTERPARTY_LIMITS("L"),
  /** Global book feed, returns only first 25 book levels */
  GLOBAL_ORDER_BOOKS("B"),
  /** Tradable book feed, returns only first 25 book levels */
  TRADABLE_ORDER_BOOKS("F"),
  SETTLEMENT_REQUESTS("R"),
  SETTLEMENT_TRANSACTIONS("N"),
  /** Positions Feed */
  POSITIONS("K"),
  ORDERS("O"),
  SETTLEMENT_ORDERS("S");

  private final String value;

  Feed(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  public static Feed fromString(String value) {
    for (Feed feed : Feed.values()) {
      if (feed.value.equals(value)) {
        return feed;
      }
    }
    return null;
  }

}
