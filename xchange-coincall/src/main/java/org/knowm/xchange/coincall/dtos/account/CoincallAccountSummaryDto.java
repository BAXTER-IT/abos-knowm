package org.knowm.xchange.coincall.dtos.account;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CoincallAccountSummaryDto {

  /**
   * Total account balance valued in BTC.
   */
  @JsonProperty("totalBtcValue")
  private BigDecimal totalBtcValue;

  /**
   * Total account balance valued in USD.
   */
  @JsonProperty("totalDollarValue")
  private BigDecimal totalDollarValue;

  /**
   * Total account balance valued in USDT.
   */
  @JsonProperty("totalUsdtValue")
  private BigDecimal totalUsdtValue;

  /**
   * Total initial margin amount for the account.
   */
  @JsonProperty("imAmount")
  private BigDecimal imAmount;

  /**
   * Total maintenance margin amount for the account.
   */
  @JsonProperty("mmAmount")
  private BigDecimal mmAmount;

  /**
   * Initial margin ratio for the account.
   */
  @JsonProperty("imRatio")
  private BigDecimal imRatio;

  /**
   * Maintenance margin ratio for the account.
   */
  @JsonProperty("mmRatio")
  private BigDecimal mmRatio;

  /**
   * Account equity including unrealized profit and loss.
   */
  @JsonProperty("equity")
  private BigDecimal equity;

  /**
   * Margin currently available for opening new positions.
   */
  @JsonProperty("availableMargin")
  private BigDecimal availableMargin;

  /**
   * Margin balance used for risk and liquidation calculations.
   */
  @JsonProperty("marginBalance")
  private BigDecimal marginBalance;

  /**
   * Total unrealized profit and loss across the account.
   */
  @JsonProperty("unrealizedPnL")
  private BigDecimal unrealizedPnL;

  @JsonProperty("accounts")
  private List<CoincallAccountDto> accounts;
}