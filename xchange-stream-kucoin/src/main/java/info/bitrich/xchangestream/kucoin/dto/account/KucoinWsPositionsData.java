package info.bitrich.xchangestream.kucoin.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.kucoin.dto.enums.KucoinMarginMode;
import info.bitrich.xchangestream.kucoin.dto.enums.KucoinPositionSide;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class KucoinWsPositionsData {

  /**
   * Symbol of the contract, e.g. XBTUSDTM.
   */
  @JsonProperty("symbol")
  private String symbol;

  /**
   * Margin mode: CROSS or ISOLATED.
   */
  @JsonProperty("marginMode")
  private KucoinMarginMode marginMode;

  /**
   * Whether it is cross margin.
   */
  @JsonProperty("crossMode")
  private Boolean crossMode;

  /**
   * ADL ranking percentile.
   */
  @JsonProperty("delevPercentage")
  private BigDecimal deleveragePercentage;

  /**
   * Open time (Unix ms).
   */
  @JsonProperty("openingTimestamp")
  private Long openingTimestamp;

  /**
   * Current timestamp (Unix ms).
   */
  @JsonProperty("currentTimestamp")
  private Long currentTimestamp;

  /**
   * Current position quantity.
   */
  @JsonProperty("currentQty")
  private BigDecimal currentQuantity;

  /**
   * Current position value.
   */
  @JsonProperty("currentCost")
  private BigDecimal currentCost;

  /**
   * Current commission.
   */
  @JsonProperty("currentComm")
  private BigDecimal currentCommission;

  /**
   * Unrealized value.
   */
  @JsonProperty("unrealisedCost")
  private BigDecimal unrealisedCost;

  /**
   * Unrealized profit and loss.
   */
  @JsonProperty("unrealisedPnl")
  private BigDecimal unrealisedProfitAndLoss;

  /**
   * Profit-loss ratio of the position.
   */
  @JsonProperty("unrealisedPnlPcnt")
  private BigDecimal unrealisedProfitAndLossPercent;

  /**
   * Rate of return on investment.
   */
  @JsonProperty("unrealisedRoePcnt")
  private BigDecimal unrealisedReturnOnEquityPercent;

  /**
   * Current realized position value.
   */
  @JsonProperty("realisedCost")
  private BigDecimal realisedCost;

  /**
   * Accumulated realized gross profit value.
   */
  @JsonProperty("realisedGrossCost")
  private BigDecimal realisedGrossCost;

  /**
   * Accumulated realized gross profit value.
   */
  @JsonProperty("realisedGrossPnl")
  private BigDecimal realisedGrossProfitAndLoss;

  /**
   * Realized profit and loss.
   */
  @JsonProperty("realisedPnl")
  private BigDecimal realisedProfitAndLoss;

  /**
   * Opened position or not.
   */
  @JsonProperty("isOpen")
  private Boolean open;

  /**
   * Mark price.
   */
  @JsonProperty("markPrice")
  private BigDecimal markPrice;

  /**
   * Mark value.
   */
  @JsonProperty("markValue")
  private BigDecimal markValue;

  /**
   * Position value.
   */
  @JsonProperty("posCost")
  private BigDecimal positionCost;

  /**
   * Initial margin. Cross = opening value / cross leverage; isolated = accumulation of initial
   * margin for each transaction.
   */
  @JsonProperty("posInit")
  private BigDecimal positionInitialMargin;

  /**
   * Position margin. Cross = mark value * imr; isolated = position margin (initial margin,
   * additional margin, funding, etc.).
   */
  @JsonProperty("posMargin")
  private BigDecimal positionMargin;

  /**
   * Average entry price.
   */
  @JsonProperty("avgEntryPrice")
  private BigDecimal averageEntryPrice;

  /**
   * Liquidation price. For Cross Margin, liquidation is based on risk rate.
   */
  @JsonProperty("liquidationPrice")
  private BigDecimal liquidationPrice;

  /**
   * Bankruptcy price. For Cross Margin, liquidation is based on risk rate.
   */
  @JsonProperty("bankruptPrice")
  private BigDecimal bankruptPrice;

  /**
   * Currency used to clear and settle the trades.
   */
  @JsonProperty("settleCurrency")
  private String settleCurrency;

  /**
   * Position side (currently BOTH).
   */
  @JsonProperty("positionSide")
  private KucoinPositionSide positionSide;

  /**
   * Leverage.
   */
  @JsonProperty("leverage")
  private BigDecimal leverage;

  /**
   * Maintenance margin requirement (optional).
   */
  @JsonProperty("maintMarginReq")
  private BigDecimal maintenanceMarginRequirement;

  /**
   * Maintenance margin (optional).
   */
  @JsonProperty("posMaint")
  private BigDecimal positionMaintenanceMargin;

  /**
   * Auto deposit margin or not (isolated only, optional).
   */
  @JsonProperty("autoDeposit")
  private Boolean autoDeposit;

  /**
   * Risk limit (isolated only, optional).
   */
  @JsonProperty("riskLimit")
  private BigDecimal riskLimit;

  /**
   * Real leverage of the order (isolated only, optional).
   */
  @JsonProperty("realLeverage")
  private BigDecimal realLeverage;

  /**
   * Added margin (isolated only, optional).
   */
  @JsonProperty("posCross")
  private BigDecimal positionCross;

  /**
   * Bankruptcy cost (isolated only, optional).
   */
  @JsonProperty("posComm")
  private BigDecimal positionCommission;

  /**
   * Funding fees paid out (isolated only, optional).
   */
  @JsonProperty("posLoss")
  private BigDecimal positionLoss;

  /**
   * Remaining unsettled funding fee for the position (isolated only, optional).
   */
  @JsonProperty("posFunding")
  private BigDecimal positionFunding;

  /**
   * Position margin (isolated only, optional).
   */
  @JsonProperty("maintMargin")
  private BigDecimal maintenanceMargin;

  /**
   * Funding time (optional, Unix ms).
   */
  @JsonProperty("fundingTime")
  private Long fundingTime;

  /**
   * Position size (optional).
   */
  @JsonProperty("qty")
  private BigDecimal quantity;

  /**
   * Funding rate (optional).
   */
  @JsonProperty("fundingRate")
  private BigDecimal fundingRate;

  /**
   * Funding fees (cumulative over position's lifecycle, optional).
   */
  @JsonProperty("fundingFee")
  private BigDecimal fundingFee;

  /**
   * Funding fee settlement time (nanoseconds, optional).
   */
  @JsonProperty("ts")
  private Long fundingFeeSettlementTime;

  /**
   * Whether risk limit adjustment succeeded (optional).
   */
  @JsonProperty("success")
  private Boolean success;

  /**
   * Failure reason for risk limit adjustment (optional).
   */
  @JsonProperty("msg")
  private String message;
}