package org.knowm.xchange.bybit.dto.account;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.bybit.dto.BybitCategory;
import org.knowm.xchange.bybit.dto.trade.BybitSide;

@Data
@Builder
@Jacksonized
public class BybitPosition {

  /**
   * Product type: linear, inverse, option.
   */
  @JsonProperty("category")
  private BybitCategory category;

  /**
   * Symbol name.
   */
  @JsonProperty("symbol")
  private String symbol;

  /**
   * Position side: Buy (long), Sell (short), "" for empty.
   */
  @JsonProperty("side")
  private BybitSide side;

  /**
   * Position size.
   */
  @JsonProperty("size")
  private BigDecimal size;

  /**
   * Position index, used to identify positions in different position modes.
   */
  @JsonProperty("positionIdx")
  private Integer positionIndex;

  /**
   * Position value.
   */
  @JsonProperty("positionValue")
  private BigDecimal positionValue;

  /**
   * Risk tier ID (0 for portfolio margin mode).
   */
  @JsonProperty("riskId")
  private Integer riskId;

  /**
   * Risk limit value (meaningless when auto risk limit is applied or PM mode).
   */
  @JsonProperty("riskLimitValue")
  private BigDecimal riskLimitValue;

  /**
   * Average entry price.
   */
  @JsonProperty("entryPrice")
  @JsonAlias("avgPrice")
  private BigDecimal entryPrice;

  /**
   * Mark price.
   */
  @JsonProperty("markPrice")
  private BigDecimal markPrice;

  /**
   * Position leverage ("" when invalid in PM mode).
   */
  @JsonProperty("leverage")
  private BigDecimal leverage;

  /**
   * Whether to add margin automatically (isolated margin mode, 0 = false, 1 = true).
   */
  @JsonProperty("autoAddMargin")
  private Integer autoAddMargin;

  /**
   * Initial margin (same as positionIMByMp, "" in PM mode).
   */
  @JsonProperty("positionIM")
  private BigDecimal positionInitialMargin;

  /**
   * Maintenance margin (same as positionMMByMp, "" in PM mode).
   */
  @JsonProperty("positionMM")
  private BigDecimal positionMaintenanceMargin;

  /**
   * Position liquidation price ("" if outside min/max or PM mode).
   */
  @JsonProperty("liqPrice")
  private BigDecimal liquidationPrice;

  /**
   * Take-profit price.
   */
  @JsonProperty("takeProfit")
  private BigDecimal takeProfitPrice;

  /**
   * Stop-loss price.
   */
  @JsonProperty("stopLoss")
  private BigDecimal stopLossPrice;

  /**
   * Trailing stop price.
   */
  @JsonProperty("trailingStop")
  private BigDecimal trailingStop;

  /**
   * Unrealised profit and loss.
   */
  @JsonProperty("unrealisedPnl")
  private BigDecimal unrealisedProfitAndLoss;

  /**
   * Realised PnL for the current holding position.
   */
  @JsonProperty("curRealisedPnl")
  private BigDecimal currentRealisedProfitAndLoss;

  /**
   * Session average price for USDC contracts.
   */
  @JsonProperty("sessionAvgPrice")
  private BigDecimal sessionAveragePrice;

  /**
   * Delta (options only).
   */
  @JsonProperty("delta")
  private BigDecimal delta;

  /**
   * Gamma (options only).
   */
  @JsonProperty("gamma")
  private BigDecimal gamma;

  /**
   * Vega (options only).
   */
  @JsonProperty("vega")
  private BigDecimal vega;

  /**
   * Theta (options only).
   */
  @JsonProperty("theta")
  private BigDecimal theta;

  /**
   * Cumulative realised PnL.
   */
  @JsonProperty("cumRealisedPnl")
  private BigDecimal cumulativeRealisedProfitAndLoss;

  /**
   * Position status: Normal, Liq, Adl.
   */
  @JsonProperty("positionStatus")
  private BybitPositionStatus positionStatus;

  /**
   * Auto-deleverage rank indicator.
   */
  @JsonProperty("adlRankIndicator")
  private Integer adlRankIndicator;

  /**
   * Whether only position reduction is allowed.
   */
  @JsonProperty("isReduceOnly")
  private Boolean reduceOnly;

  /**
   * First time this position was created on this symbol (Unix ms).
   */
  @JsonProperty("createdTime")
  private String createdTime;

  /**
   * Position data updated timestamp (Unix ms).
   */
  @JsonProperty("updatedTime")
  private String updatedTime;

  /**
   * Cross sequence for associating fills and position updates.
   */
  @JsonProperty("seq")
  private Long sequence;

  /**
   * Timestamp when MMR will be/was adjusted by system (ms).
   */
  @JsonProperty("mmrSysUpdatedTime")
  private String maintenanceMarginSystemUpdatedTime;

  /**
   * Timestamp when leverage will be/was adjusted by system (ms).
   */
  @JsonProperty("leverageSysUpdatedTime")
  private String leverageSystemUpdatedTime;

  /**
   * Initial margin calculated by mark price (same as positionIM, "" in PM mode).
   */
  @JsonProperty("positionIMByMp")
  private BigDecimal positionInitialMarginByMarkPrice;

  /**
   * Maintenance margin calculated by mark price (same as positionMM, "" in PM mode).
   */
  @JsonProperty("positionMMByMp")
  private BigDecimal positionMaintenanceMarginByMarkPrice;

  /**
   * TPSL mode (deprecated, always "Full").
   */
  @JsonProperty("tpslMode")
  private String takeProfitStopLossMode;

  /**
   * Bust price (deprecated, always "").
   */
  @JsonProperty("bustPrice")
  private String bustPrice;

  /**
   * Position balance (deprecated, refer to positionIM or positionIMByMp).
   */
  @JsonProperty("positionBalance")
  private BigDecimal positionBalance;

  /**
   * Trade mode (deprecated, always 0).
   */
  @JsonProperty("tradeMode")
  private Integer tradeMode;

}
