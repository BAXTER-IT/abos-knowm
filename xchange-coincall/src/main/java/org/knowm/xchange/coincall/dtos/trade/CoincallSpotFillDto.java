package org.knowm.xchange.coincall.dtos.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.coincall.dtos.enums.CoincallTradeSide;
import org.knowm.xchange.utils.jackson.RawJsonAware;

@Data
@Builder
@Jacksonized
public class CoincallSpotFillDto implements RawJsonAware {

  /**
   * Internal fill ID
   */
  @JsonProperty("id")
  private final long id;

  /**
   * Client-defined order ID
   */
  @JsonProperty("clientOrderId")
  private final String clientOrderId;

  /**
   * Exchange-assigned order ID
   */
  @JsonProperty("orderId")
  private final long orderId;

  /**
   * Trade ID for this fill
   */
  @JsonProperty("tradeId")
  private final String tradeId;

  /**
   * User ID
   */
  @JsonProperty("userId")
  private final long userId;

  /**
   * Symbol (e.g. XRPUSDT)
   */
  @JsonProperty("symbol")
  private final String symbol;

  /**
   * Symbol display string (e.g. XRP/USDT)
   */
  @JsonProperty("displaySymbol")
  private final String displaySymbol;

  /**
   * Trade side: 1=Buy, 2=Sell (according to Coincall spec)
   */
  @JsonProperty("tradeSide")
  private final CoincallTradeSide tradeSide;

  /**
   * Executed price
   */
  @JsonProperty("price")
  private final BigDecimal price;

  /**
   * Executed quantity
   */
  @JsonProperty("qty")
  private final BigDecimal quantity;

  /**
   * Whether user was taker (or maker if false)
   */
  @JsonProperty("isTaker")
  private final boolean isTaker;

  /**
   * Fee amount
   */
  @JsonProperty("fee")
  private final BigDecimal fee;

  /**
   * Fee currency (e.g., XRP)
   */
  @JsonProperty("feeCurrency")
  private final String feeCurrency;

  /**
   * Creation time (ms)
   */
  @JsonProperty("ts")
  private final long timestamp;

  /**
   * Last update time (ms)
   */
  @JsonProperty("updateTime")
  private final long updateTime;

  private String rawJson;
}