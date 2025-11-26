package org.knowm.xchange.bybit.dto.account.walletbalance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.currency.Currency;

@Builder
@Jacksonized
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class BybitCoinWalletBalance {

  @JsonProperty("coin")
  Currency coin;

  @JsonProperty("equity")
  BigDecimal equity;

  @JsonProperty("usdValue")
  BigDecimal usdValue;

  @JsonProperty("walletBalance")
  BigDecimal walletBalance;

  @JsonProperty("locked")
  BigDecimal locked;

  /**
   * Spot asset qty that is used as hedge (portfolio margin).
   */
  @JsonProperty("spotHedgingQty")
  String spotHedgingQty;

  /**
   * Total liabilities from spot + derivatives.
   */
  @JsonProperty("borrowAmount")
  String borrowAmount;

  /**
   * Borrow amount from spot margin/manual borrow (not deprecated).
   */
  @JsonProperty("spotBorrow")
  String spotBorrow;

  @JsonProperty("accruedInterest")
  String accruedInterest;

  @JsonProperty("totalOrderIM")
  String totalOrderIM;

  @JsonProperty("totalPositionIM")
  String totalPositionIM;

  @JsonProperty("totalPositionMM")
  String totalPositionMM;

  @JsonProperty("unrealisedPnl")
  String unrealisedPnl;

  @JsonProperty("cumRealisedPnl")
  String cumRealisedPnl;

  @JsonProperty("bonus")
  String bonus;

  /**
   * Platform-level collateral eligibility.
   */
  @JsonProperty("collateralSwitch")
  Boolean collateralSwitch;

  /**
   * User-level collateral switch.
   */
  @JsonProperty("marginCollateral")
  Boolean marginCollateral;
}