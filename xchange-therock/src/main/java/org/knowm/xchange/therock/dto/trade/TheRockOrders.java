package org.knowm.xchange.therock.dto.trade;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/** @author Pnk */
@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
public class TheRockOrders {

  private TheRockOrder[] orders;
  private TheRockMeta meta;

  protected TheRockOrders() {}

  public TheRockOrders(TheRockOrder[] orders) {
    this.orders = orders;
  }

  public TheRockOrder[] getOrders() {
    return orders;
  }

  public TheRockMeta getMeta() {
    return meta;
  }
}
