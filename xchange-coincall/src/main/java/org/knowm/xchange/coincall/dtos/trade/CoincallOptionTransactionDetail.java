package org.knowm.xchange.coincall.dtos.trade;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.coincall.dtos.enums.CoincallTradeSide;
import org.knowm.xchange.coincall.dtos.enums.CoincallTradeType;
import org.knowm.xchange.utils.jackson.RawJsonAware;

@Data
@Builder
@Jacksonized
public class CoincallOptionTransactionDetail implements RawJsonAware {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("optionId")
  private Long optionId;

  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("tradeSide")
  private CoincallTradeSide tradeSide;

  @JsonProperty("tradeType")
  private CoincallTradeType tradeType;

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("qty")
  private BigDecimal qty;

  /**
   * Implied volatility at the moment of the trade.
   */
  @JsonProperty("iv")
  private BigDecimal iv;

  @JsonProperty("markPrice")
  private BigDecimal markPrice;

  @JsonProperty("indexPrice")
  private BigDecimal indexPrice;

  @JsonProperty("orderId")
  private Long orderId;

  @JsonProperty("tradeId")
  private Long tradeId;

  @JsonProperty("fee")
  private BigDecimal fee;

  /**
   * Unix timestamp (milliseconds) of the transaction.
   */
  @JsonProperty("time")
  private Long time;

  /**
   * Whether the order reduces an existing position.
   */
  @JsonProperty("reduceOnly")
  private boolean reduceOnly;

  @JsonProperty("isTaker")
  private boolean isTaker;

  /**
   * Market Maker Protection flag indicating MMP-triggered activity.
   */
  @JsonProperty("mmp")
  private Boolean mmp;

  private String rawJson;
}