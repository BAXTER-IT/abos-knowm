package org.knowm.xchange.okex.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.okex.dto.enums.OkexInstrumentType;
import org.knowm.xchange.okex.dto.enums.OkexMarginMode;
import org.knowm.xchange.okex.dto.enums.OkexPositionSide;

/** https://www.okx.com/docs-v5/en/#rest-api-account-get-positions */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OkexPosition {

  /** Instrument type. */
  @JsonProperty("instType")
  private OkexInstrumentType instrumentType;

  /** Margin mode (cross or isolated). */
  @JsonProperty("mgnMode")
  private OkexMarginMode marginMode;

  /** Position ID. */
  @JsonProperty("posId")
  private String positionId;

  /** Position side (long, short, net). */
  @JsonProperty("posSide")
  private OkexPositionSide positionSide;

  /** Quantity of positions. */
  @JsonProperty("pos")
  private BigDecimal positionSize;

  /** Hedged position size for delta-neutral strategy. */
  @JsonProperty("hedgedPos")
  private BigDecimal hedgedPositionSize;

  /** Base currency balance (deprecated, Quick Margin Mode). */
  @JsonProperty("baseBal")
  private BigDecimal baseCurrencyBalance;

  /** Quote currency balance (deprecated, Quick Margin Mode). */
  @JsonProperty("quoteBal")
  private BigDecimal quoteCurrencyBalance;

  /** Base currency amount borrowed (deprecated, Quick Margin Mode). */
  @JsonProperty("baseBorrowed")
  private BigDecimal baseCurrencyBorrowed;

  /** Base interest incurred (deprecated, Quick Margin Mode). */
  @JsonProperty("baseInterest")
  private BigDecimal baseCurrencyInterest;

  /** Quote currency amount borrowed (deprecated, Quick Margin Mode). */
  @JsonProperty("quoteBorrowed")
  private BigDecimal quoteCurrencyBorrowed;

  /** Quote interest incurred (deprecated, Quick Margin Mode). */
  @JsonProperty("quoteInterest")
  private BigDecimal quoteCurrencyInterest;

  /** Position currency for margin positions. */
  @JsonProperty("posCcy")
  private String positionCurrency;

  /** Position that can be closed. */
  @JsonProperty("availPos")
  private BigDecimal availablePosition;

  /** Average open price. */
  @JsonProperty("avgPx")
  private BigDecimal averageOpenPrice;

  /** Unrealized P&L by mark price. */
  @JsonProperty("upl")
  private BigDecimal unrealizedProfitAndLoss;

  /** Unrealized P&L ratio by mark price. */
  @JsonProperty("uplRatio")
  private BigDecimal unrealizedProfitAndLossRatio;

  /** Unrealized P&L by last price (display use). */
  @JsonProperty("uplLastPx")
  private BigDecimal unrealizedProfitAndLossLastPrice;

  /** Unrealized P&L ratio by last price. */
  @JsonProperty("uplRatioLastPx")
  private BigDecimal unrealizedProfitAndLossRatioLastPrice;

  /** Instrument ID, e.g. BTC-USDT-SWAP. */
  @JsonProperty("instId")
  private String instrumentId;

  /** Leverage (not applicable to OPTION seller). */
  @JsonProperty("lever")
  private BigDecimal leverage;

  /** Estimated liquidation price (not for OPTIONS). */
  @JsonProperty("liqPx")
  private BigDecimal liquidationPrice;

  /** Latest mark price. */
  @JsonProperty("markPx")
  private BigDecimal markPrice;

  /** Initial margin requirement (cross margin only). */
  @JsonProperty("imr")
  private BigDecimal initialMarginRequirement;

  /** Margin amount (isolated margin only). */
  @JsonProperty("margin")
  private BigDecimal margin;

  /** Maintenance margin ratio. */
  @JsonProperty("mgnRatio")
  private BigDecimal maintenanceMarginRatio;

  /** Maintenance margin requirement. */
  @JsonProperty("mmr")
  private BigDecimal maintenanceMarginRequirement;

  /** Liabilities for margin. */
  @JsonProperty("liab")
  private BigDecimal liabilities;

  /** Liabilities currency for margin. */
  @JsonProperty("liabCcy")
  private String liabilitiesCurrency;

  /** Interest accrued but not settled. */
  @JsonProperty("interest")
  private BigDecimal interestAccrued;

  /** Last trade ID affecting this position. */
  @JsonProperty("tradeId")
  private String lastTradeId;

  /** Notional value of position in USD. */
  @JsonProperty("notionalUsd")
  private BigDecimal notionalUsdValue;

  /** Option value (OPTIONS only). */
  @JsonProperty("optVal")
  private BigDecimal optionValue;

  /** Liability amount of close orders in isolated margin. */
  @JsonProperty("pendingCloseOrdLiabVal")
  private BigDecimal pendingCloseOrderLiabilityValue;

  /** Auto-deleveraging signal level (0–5). */
  @JsonProperty("adl")
  private String autoDeleveragingLevel;

  /** External business ID, e.g. experience coupon ID. */
  @JsonProperty("bizRefId")
  private String businessReferenceId;

  /** External business type. */
  @JsonProperty("bizRefType")
  private String businessReferenceType;

  /** Currency used for margin. */
  @JsonProperty("ccy")
  private String marginCurrency;

  /** Latest traded price. */
  @JsonProperty("last")
  private BigDecimal lastTradedPrice;

  /** Latest underlying index price. */
  @JsonProperty("idxPx")
  private BigDecimal indexPrice;

  /** Latest USD price of the currency (FUTURES/SWAP/OPTION). */
  @JsonProperty("usdPx")
  private BigDecimal usdPrice;

  /** Breakeven price. */
  @JsonProperty("bePx")
  private BigDecimal breakevenPrice;

  /** Delta in dollars (Black–Scholes, OPTIONS only). */
  @JsonProperty("deltaBS")
  private BigDecimal deltaBlackScholesDollars;

  /** Delta in coins (OPTIONS only). */
  @JsonProperty("deltaPA")
  private BigDecimal deltaPortfolioCoins;

  /** Gamma in dollars (Black–Scholes, OPTIONS only). */
  @JsonProperty("gammaBS")
  private BigDecimal gammaBlackScholesDollars;

  /** Gamma in coins (OPTIONS only). */
  @JsonProperty("gammaPA")
  private BigDecimal gammaPortfolioCoins;

  /** Theta in dollars (Black–Scholes, OPTIONS only). */
  @JsonProperty("thetaBS")
  private BigDecimal thetaBlackScholesDollars;

  /** Theta in coins (OPTIONS only). */
  @JsonProperty("thetaPA")
  private BigDecimal thetaPortfolioCoins;

  /** Vega in dollars (Black–Scholes, OPTIONS only). */
  @JsonProperty("vegaBS")
  private BigDecimal vegaBlackScholesDollars;

  /** Vega in coins (OPTIONS only). */
  @JsonProperty("vegaPA")
  private BigDecimal vegaPortfolioCoins;

  /** Spot in use amount (Portfolio margin). */
  @JsonProperty("spotInUseAmt")
  private BigDecimal spotInUseAmount;

  /** Spot in use currency (Portfolio margin). */
  @JsonProperty("spotInUseCcy")
  private String spotInUseCurrency;

  /** User-defined spot risk offset amount (Portfolio margin). */
  @JsonProperty("clSpotInUseAmt")
  private BigDecimal closingSpotInUseAmount;

  /** Max possible spot risk offset amount (Portfolio margin). */
  @JsonProperty("maxSpotInUseAmt")
  private BigDecimal maximumSpotInUseAmount;

  /** Realized P&L (FUTURES/SWAP/OPTION). */
  @JsonProperty("realizedPnl")
  private BigDecimal realizedProfitAndLoss;

  /** Accumulated P&L of closing orders (excluding fee). */
  @JsonProperty("pnl")
  private BigDecimal accumulatedProfitAndLoss;

  /** Accumulated fee (negative = fee, positive = rebate). */
  @JsonProperty("fee")
  private BigDecimal accumulatedFee;

  /** Accumulated funding fee. */
  @JsonProperty("fundingFee")
  private BigDecimal accumulatedFundingFee;

  /** Accumulated liquidation penalty (negative when present). */
  @JsonProperty("liqPenalty")
  private BigDecimal accumulatedLiquidationPenalty;

  /** Close position algo orders attached to this position. */
  @JsonProperty("closeOrderAlgo")
  private List<OkexCloseOrderAlgoDto> closeOrderAlgorithms;

  /** Creation time, Unix ms timestamp. */
  @JsonProperty("cTime")
  private String creationTime;

  /** Latest time position was adjusted, Unix ms timestamp. */
  @JsonProperty("uTime")
  private String updateTime;

  /** Push time of the position information, Unix ms timestamp. */
  @JsonProperty("pTime")
  private String pushTime;

  /** Non-settlement entry price (FUTURES cross). */
  @JsonProperty("nonSettleAvgPx")
  private BigDecimal nonSettlementAveragePrice;

  /** Accumulated settled P&L (by settlement price, FUTURES cross). */
  @JsonProperty("settledPnl")
  private BigDecimal accumulatedSettledProfitAndLoss;
}
