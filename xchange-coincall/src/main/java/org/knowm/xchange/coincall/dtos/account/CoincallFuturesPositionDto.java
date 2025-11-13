package org.knowm.xchange.coincall.dtos.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CoincallFuturesPositionDto {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("positionId")
  private Long positionId;

  /**
   * Last update timestamp in milliseconds.
   */
  @JsonProperty("updateTime")
  private Long updateTime;

  @JsonProperty("userId")
  private Long userId;

  /**
   * Notional value of the position.
   */
  @JsonProperty("value")
  private BigDecimal value;

  @JsonProperty("qty")
  private BigDecimal qty;

  /**
   * Initial margin required for this position.
   */
  @JsonProperty("initMargin")
  private BigDecimal initMargin;

  /**
   * Maintenance margin level for the position.
   */
  @JsonProperty("maintMargin")
  private BigDecimal maintMargin;

  @JsonProperty("avgPrice")
  private BigDecimal avgPrice;

  @JsonProperty("markPrice")
  private BigDecimal markPrice;

  /**
   * Estimated liquidation price (may be null if not applicable).
   */
  @JsonProperty("elp")
  private BigDecimal elp;

  /**
   * Return on investment for this position.
   */
  @JsonProperty("roi")
  private BigDecimal roi;

  /**
   * Unrealized profit and loss.
   */
  @JsonProperty("upnl")
  private BigDecimal upnl;

  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("tradeSide")
  private Integer tradeSide;

  @JsonProperty("leverage")
  private Integer leverage;

  /**
   * Position delta (exposure).
   */
  @JsonProperty("delta")
  private BigDecimal delta;

  @JsonProperty("baseToken")
  private String baseToken;

  @JsonProperty("quoteToken")
  private String quoteToken;
}
