package info.bitrich.xchangestream.kucoin.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.currency.Currency;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class KucoinWsFuturesBalanceData {

  /**
   * Cross position margin.
   */
  @JsonProperty("crossPosMargin")
  private BigDecimal crossPositionMargin;

  /**
   * Isolated order margin.
   */
  @JsonProperty("isolatedOrderMargin")
  private BigDecimal isolatedOrderMargin;

  /**
   * Held (locked) balance.
   */
  @JsonProperty("holdBalance")
  private BigDecimal holdBalance;

  /**
   * Equity.
   */
  @JsonProperty("equity")
  private BigDecimal equity;

  /**
   * Version (sequence/version number).
   */
  @JsonProperty("version")
  private String version;

  /**
   * Available balance.
   */
  @JsonProperty("availableBalance")
  private BigDecimal availableBalance;

  /**
   * Isolated position margin.
   */
  @JsonProperty("isolatedPosMargin")
  private BigDecimal isolatedPositionMargin;

  /**
   * Maximum withdrawable amount.
   */
  @JsonProperty("maxWithdrawAmount")
  private BigDecimal maxWithdrawAmount;

  /**
   * Wallet balance.
   */
  @JsonProperty("walletBalance")
  private BigDecimal walletBalance;

  /**
   * Isolated funding fee margin.
   */
  @JsonProperty("isolatedFundingFeeMargin")
  private BigDecimal isolatedFundingFeeMargin;

  /**
   * Cross unrealised PnL.
   */
  @JsonProperty("crossUnPnl")
  private BigDecimal crossUnrealisedPnl;

  /**
   * Total cross margin.
   */
  @JsonProperty("totalCrossMargin")
  private BigDecimal totalCrossMargin;

  /**
   * Currency, e.g. USDT.
   */
  @JsonProperty("currency")
  private Currency currency;

  /**
   * Isolated unrealised PnL.
   */
  @JsonProperty("isolatedUnPnl")
  private BigDecimal isolatedUnrealisedPnl;

  /**
   * Cross order margin.
   */
  @JsonProperty("crossOrderMargin")
  private BigDecimal crossOrderMargin;

  /**
   * Event timestamp (Unix ms, as string).
   */
  @JsonProperty("timestamp")
  private Long timestamp;
}
