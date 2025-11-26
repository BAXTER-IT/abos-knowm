package info.bitrich.xchangestream.gateio.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PositionPayload {

  /**
   * Contract name (delivery or futures contract).
   */
  @JsonProperty("contract")
  private String contract;

  /**
   * Cross margin leverage limit. Present in some responses (e.g. futures), null otherwise.
   */
  @JsonProperty("cross_leverage_limit")
  private BigDecimal crossLeverageLimit;

  /**
   * Average entry price.
   */
  @JsonProperty("entry_price")
  private BigDecimal entryPrice;

  /**
   * Historical realized PnL.
   */
  @JsonProperty("history_pnl")
  private BigDecimal historyPnl;

  /**
   * Historical realized POINT PnL.
   */
  @JsonProperty("history_point")
  private BigDecimal historyPointPnl;

  /**
   * PnL of the last position close.
   */
  @JsonProperty("last_close_pnl")
  private BigDecimal lastClosePnl;

  /**
   * Position leverage. 0 means cross margin; positive number means isolated margin.
   */
  @JsonProperty("leverage")
  private BigDecimal leverage;

  /**
   * Maximum leverage under current risk limit.
   */
  @JsonProperty("leverage_max")
  private BigDecimal maximumLeverage;

  /**
   * Liquidation price.
   */
  @JsonProperty("liq_price")
  private BigDecimal liquidationPrice;

  /**
   * Maintenance rate under the current risk limit.
   */
  @JsonProperty("maintenance_rate")
  private BigDecimal maintenanceRate;

  /**
   * Position margin.
   */
  @JsonProperty("margin")
  private BigDecimal margin;

  /**
   * Realized PnL.
   */
  @JsonProperty("realised_pnl")
  private BigDecimal realisedPnl;

  /**
   * Realized POINT PnL.
   */
  @JsonProperty("realised_point")
  private BigDecimal realisedPointPnl;

  /**
   * Position risk limit.
   */
  @JsonProperty("risk_limit")
  private Integer riskLimit;

  /**
   * Contract size.
   */
  @JsonProperty("size")
  private BigDecimal size;

  /**
   * Update unix timestamp (seconds).
   */
  @JsonProperty("time")
  private Long time;

  /**
   * Update unix timestamp in milliseconds.
   */
  @JsonProperty("time_ms")
  private Long timeMs;

  /**
   * User ID.
   */
  @JsonProperty("user")
  private String userId;

  /**
   * Message sequence number (if provided). Present in futures response, absent/null in others.
   */
  @JsonProperty("update_id")
  private Integer updateId;
}

