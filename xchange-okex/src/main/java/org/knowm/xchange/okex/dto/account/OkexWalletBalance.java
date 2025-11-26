package org.knowm.xchange.okex.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.okex.dto.enums.OkexAutoLendStatus;
import org.knowm.xchange.okex.dto.enums.OkexCollateralRestrictionStatus;
import org.knowm.xchange.okex.dto.enums.OkexDeltaNeutralStatus;
import org.knowm.xchange.okex.dto.enums.OkexForcedRepaymentType;

/**
 * <a href="https://www.okx.com/docs-v5/en/#trading-account-rest-api-get-balance">https://www.okx.com/docs-v5/en/#trading-account-rest-api-get-balance</a>
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OkexWalletBalance {

  /**
   * Update time of account information (Unix ms).
   */
  @JsonProperty("uTime")
  private String updateTime;

  /**
   * Total equity in USD.
   */
  @JsonProperty("totalEq")
  private BigDecimal totalEquityUsd;

  /**
   * Isolated margin equity in USD.
   */
  @JsonProperty("isoEq")
  private BigDecimal isolatedMarginEquityUsd;

  /**
   * Adjusted / effective equity in USD.
   */
  @JsonProperty("adjEq")
  private BigDecimal adjustedEquityUsd;

  /**
   * Account-level available equity in USD.
   */
  @JsonProperty("availEq")
  private BigDecimal availableEquityUsd;

  /**
   * Cross margin frozen for pending orders in USD.
   */
  @JsonProperty("ordFroz")
  private BigDecimal crossMarginFrozenUsd;

  /**
   * Initial margin requirement in USD.
   */
  @JsonProperty("imr")
  private BigDecimal initialMarginRequirementUsd;

  /**
   * Maintenance margin requirement in USD.
   */
  @JsonProperty("mmr")
  private BigDecimal maintenanceMarginRequirementUsd;

  /**
   * Potential borrowing IMR of the account in USD.
   */
  @JsonProperty("borrowFroz")
  private BigDecimal potentialBorrowingInitialMarginUsd;

  /**
   * Maintenance margin ratio in USD.
   */
  @JsonProperty("mgnRatio")
  private BigDecimal maintenanceMarginRatioUsd;

  /**
   * Notional value of positions in USD.
   */
  @JsonProperty("notionalUsd")
  private BigDecimal notionalValueUsd;

  /**
   * Notional value for borrow in USD.
   */
  @JsonProperty("notionalUsdForBorrow")
  private BigDecimal notionalValueForBorrowUsd;

  /**
   * Notional value of positions for perpetual futures in USD.
   */
  @JsonProperty("notionalUsdForSwap")
  private BigDecimal notionalValueForSwapUsd;

  /**
   * Notional value of positions for expiry futures in USD.
   */
  @JsonProperty("notionalUsdForFutures")
  private BigDecimal notionalValueForFuturesUsd;

  /**
   * Notional value of positions for options in USD.
   */
  @JsonProperty("notionalUsdForOption")
  private BigDecimal notionalValueForOptionUsd;

  /**
   * Account-level unrealized P&L under cross margin in USD.
   */
  @JsonProperty("upl")
  private BigDecimal unrealizedProfitAndLossUsd;

  /**
   * Delta in USD.
   */
  @JsonProperty("delta")
  private BigDecimal deltaUsd;

  /**
   * Delta neutral strategy account-level delta leverage.
   */
  @JsonProperty("deltaLever")
  private BigDecimal deltaLeverage;

  /**
   * Delta risk status (0 normal, 1 transfer restricted, 2 delta reducing).
   */
  @JsonProperty("deltaNeutralStatus")
  private OkexDeltaNeutralStatus deltaNeutralStatus;

  /**
   * Detailed asset information for all currencies.
   */
  @JsonProperty("details")
  private List<OkexBalanceDetailDto> details;

  /**
   * Detailed asset information for a single currency.
   */
  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OkexBalanceDetailDto {

    /**
     * Currency code.
     */
    @JsonProperty("ccy")
    private String currency;

    /**
     * Equity of the currency.
     */
    @JsonProperty("eq")
    private BigDecimal equity;

    /**
     * Cash balance of the currency.
     */
    @JsonProperty("cashBal")
    private BigDecimal cashBalance;

    /**
     * Update time of currency balance (Unix ms).
     */
    @JsonProperty("uTime")
    private String updateTime;

    /**
     * Isolated margin equity of the currency.
     */
    @JsonProperty("isoEq")
    private BigDecimal isolatedMarginEquity;

    /**
     * Available equity of the currency.
     */
    @JsonProperty("availEq")
    private BigDecimal availableEquity;

    /**
     * Discount equity of the currency in USD.
     */
    @JsonProperty("disEq")
    private BigDecimal discountEquityUsd;

    /**
     * Frozen balance for Dip Sniper and Peak Sniper.
     */
    @JsonProperty("fixedBal")
    private BigDecimal fixedBalance;

    /**
     * Available balance of the currency.
     */
    @JsonProperty("availBal")
    private BigDecimal availableBalance;

    /**
     * Frozen balance of the currency.
     */
    @JsonProperty("frozenBal")
    private BigDecimal frozenBalance;

    /**
     * Margin frozen for open orders.
     */
    @JsonProperty("ordFrozen")
    private BigDecimal orderFrozenMargin;

    /**
     * Liabilities of the currency (positive value).
     */
    @JsonProperty("liab")
    private BigDecimal liabilities;

    /**
     * Sum of unrealized P&L of all margin and derivatives positions for this currency.
     */
    @JsonProperty("upl")
    private BigDecimal unrealizedProfitAndLoss;

    /**
     * Liabilities due to unrealized loss of this currency.
     */
    @JsonProperty("uplLiab")
    private BigDecimal unrealizedLossLiabilities;

    /**
     * Cross liabilities of the currency.
     */
    @JsonProperty("crossLiab")
    private BigDecimal crossLiabilities;

    /**
     * Trial fund balance for this currency.
     */
    @JsonProperty("rewardBal")
    private BigDecimal trialFundBalance;

    /**
     * Isolated liabilities of the currency.
     */
    @JsonProperty("isoLiab")
    private BigDecimal isolatedLiabilities;

    /**
     * Cross maintenance margin ratio of the currency.
     */
    @JsonProperty("mgnRatio")
    private BigDecimal maintenanceMarginRatio;

    /**
     * Cross initial margin requirement at the currency level.
     */
    @JsonProperty("imr")
    private BigDecimal initialMarginRequirement;

    /**
     * Cross maintenance margin requirement at the currency level.
     */
    @JsonProperty("mmr")
    private BigDecimal maintenanceMarginRequirement;

    /**
     * Accrued interest of the currency (positive value).
     */
    @JsonProperty("interest")
    private BigDecimal interestAccrued;

    /**
     * Risk indicator of forced repayment (0–5).
     */
    @JsonProperty("twap")
    private BigDecimal forcedRepaymentRiskLevel;

    /**
     * Forced repayment (FRP) type (0 no FRP, 1 user-based, 2 platform-based).
     */
    @JsonProperty("frpType")
    private OkexForcedRepaymentType forcedRepaymentType;

    /**
     * Max loan of the currency.
     */
    @JsonProperty("maxLoan")
    private BigDecimal maxLoan;

    /**
     * Equity in USD of the currency.
     */
    @JsonProperty("eqUsd")
    private BigDecimal equityUsd;

    /**
     * Potential borrowing IMR of the currency in USD.
     */
    @JsonProperty("borrowFroz")
    private BigDecimal potentialBorrowingInitialMarginUsd;

    /**
     * Leverage of the currency (Futures mode).
     */
    @JsonProperty("notionalLever")
    private BigDecimal notionalLeverage;

    /**
     * Strategy equity.
     */
    @JsonProperty("stgyEq")
    private BigDecimal strategyEquity;

    /**
     * Isolated unrealized P&L of the currency.
     */
    @JsonProperty("isoUpl")
    private BigDecimal isolatedUnrealizedProfitAndLoss;

    /**
     * Spot in use amount (Portfolio margin).
     */
    @JsonProperty("spotInUseAmt")
    private BigDecimal spotInUseAmount;

    /**
     * User-defined spot risk offset amount (Portfolio margin).
     */
    @JsonProperty("clSpotInUseAmt")
    private BigDecimal closingSpotInUseAmount;

    /**
     * Max possible spot risk offset amount (Portfolio margin).
     */
    @JsonProperty("maxSpotInUse")
    private BigDecimal maximumSpotInUseAmount;

    /**
     * Spot isolated balance (copy trading).
     */
    @JsonProperty("spotIsoBal")
    private BigDecimal spotIsolatedBalance;

    /**
     * Smart sync equity, only for copy trader.
     */
    @JsonProperty("smtSyncEq")
    private BigDecimal smartSyncEquity;

    /**
     * Spot smart sync equity, only for copy trader.
     */
    @JsonProperty("spotCopyTradingEq")
    private BigDecimal spotCopyTradingEquity;

    /**
     * Spot balance (in currency units).
     */
    @JsonProperty("spotBal")
    private BigDecimal spotBalance;

    /**
     * Spot average cost price (USD).
     */
    @JsonProperty("openAvgPx")
    private BigDecimal spotAverageCostPriceUsd;

    /**
     * Spot accumulated cost price (USD).
     */
    @JsonProperty("accAvgPx")
    private BigDecimal spotAccumulatedCostPriceUsd;

    /**
     * Spot unrealized profit and loss (USD).
     */
    @JsonProperty("spotUpl")
    private BigDecimal spotUnrealizedProfitAndLossUsd;

    /**
     * Spot unrealized profit and loss ratio.
     */
    @JsonProperty("spotUplRatio")
    private BigDecimal spotUnrealizedProfitAndLossRatio;

    /**
     * Spot accumulated profit and loss (USD).
     */
    @JsonProperty("totalPnl")
    private BigDecimal spotTotalProfitAndLossUsd;

    /**
     * Spot accumulated profit and loss ratio.
     */
    @JsonProperty("totalPnlRatio")
    private BigDecimal spotTotalProfitAndLossRatio;

    /**
     * Platform-level collateral restriction status.
     */
    @JsonProperty("colRes")
    private OkexCollateralRestrictionStatus collateralRestrictionStatus;

    /**
     * Auto conversion risk indicator level (0–5).
     */
    @JsonProperty("colBorrAutoConversion")
    private BigDecimal collateralBorrowAutoConversionRiskLevel;

    /**
     * Platform-level collateralized borrow restriction (deprecated, use colRes).
     */
    @JsonProperty("collateralRestrict")
    private Boolean collateralBorrowRestricted;

    /**
     * Whether this currency is enabled as collateral (multi-currency margin).
     */
    @JsonProperty("collateralEnabled")
    private Boolean collateralEnabled;

    /**
     * Auto lend status (unsupported/off/pending/active).
     */
    @JsonProperty("autoLendStatus")
    private OkexAutoLendStatus autoLendStatus;

    /**
     * Auto lend matched amount of the currency.
     */
    @JsonProperty("autoLendMtAmt")
    private BigDecimal autoLendMatchedAmount;
  }
}
