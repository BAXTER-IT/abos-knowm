package org.knowm.xchange.finerymarkets.dto;

public enum OrderType {
  LIMIT,
  POST_ONLY,
  LIMIT_IOC,
  LIMIT_FOK,
  MARKET_IOC,
  MARKET_FOK,
  MANUAL_TRADE,
  LIQUIDATION_TRADE
}
