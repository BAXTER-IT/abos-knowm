/** Copyright 2019 Mek Global Limited. */
package org.knowm.xchange.kucoin.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.ToString;
import org.knowm.xchange.kucoin.config.deserializer.TradeResponseDeserializer;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = TradeResponseDeserializer.class)
public class TradeResponse {

  private String symbol;

  private String tradeId;

  private String orderId;

  private String counterOrderId;

  private String side;

  public String getSide() {
    return this.side == null ? null : this.side.toLowerCase();
  }

  private Liquidity liquidity;

  private boolean forceTaker;

  private BigDecimal price;

  private BigDecimal size;

  private BigDecimal funds;

  private BigDecimal fee;

  private BigDecimal feeRate;

  private String feeCurrency;

  private String domainId;

  // Spot rows call it "type", futures rows "orderType".
  @JsonProperty("type")
  @JsonAlias("orderType")
  private String orderType;

  private String stop;

  @JsonProperty("createdAt")
  private Date tradeCreatedAt;

  private String displayType;

  /** The whole row as parsed, fields the typed DTO does not know included (see TradeResponseDeserializer). */
  private String rawJson;

  public static enum Liquidity {
    @JsonProperty("taker")
    TAKER,

    @JsonProperty("maker")
    MAKER
  }

}
