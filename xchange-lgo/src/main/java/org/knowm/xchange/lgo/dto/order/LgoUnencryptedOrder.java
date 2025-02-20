package org.knowm.xchange.lgo.dto.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LgoUnencryptedOrder {

  public final String type;
  public final String side;
  public final String productId;
  public final String quantity;
  public final String price;
  public final long timestamp;

  public LgoUnencryptedOrder(
      String type, String side, String productId, String quantity, String price, long timestamp) {
    this.type = type;
    this.side = side;
    this.productId = productId;
    this.quantity = quantity;
    this.price = price;
    this.timestamp = timestamp;
  }
}
