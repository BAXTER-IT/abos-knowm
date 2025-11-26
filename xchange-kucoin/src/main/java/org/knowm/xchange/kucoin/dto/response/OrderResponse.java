/** Copyright 2019 Mek Global Limited. */
package org.knowm.xchange.kucoin.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {

  private String id;

  private String clientOid;

  private String symbol;

  private String opType;

  private String type;

  private String side;

  private BigDecimal price;

  private BigDecimal size;

  private BigDecimal funds;

  private BigDecimal dealSize;

  private BigDecimal dealFunds;

  private BigDecimal fee;

  private String feeCurrency;

  private String stp;

  private String timeInForce;

  private Boolean postOnly;

  private Boolean hidden;

  private Boolean iceberg;

  private BigDecimal visibleSize;

  private Long cancelAfter;

  private String channel;

  private String remark;

  private String tags;

  private Boolean cancelExist;

  private String tradeType;

  private Boolean inOrderBook;

  private BigDecimal cancelledSize;

  private BigDecimal cancelledFunds;

  private BigDecimal remainSize;

  private BigDecimal remainFunds;

  private BigDecimal tax;

  @JsonProperty("active")
  private Boolean isActive;

  private Date createdAt;

  private Date lastUpdatedAt;

  private String stop;

  private Boolean stopTriggered;

  private BigDecimal stopPrice;



  public String getType() {
    return this.type == null ? null : this.type.toLowerCase();
  }

  public String getSide() {
    return this.side == null ? null : this.side.toLowerCase();
  }

  public String getStop() {
    return this.stop == null ? null : this.stop.toLowerCase();
  }
}
