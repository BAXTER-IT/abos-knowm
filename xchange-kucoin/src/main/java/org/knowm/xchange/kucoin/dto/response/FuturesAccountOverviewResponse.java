package org.knowm.xchange.kucoin.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Data;

/**
 * The futures wallet for a single currency — {@code GET /api/v1/account-overview}.
 *
 * <p>This endpoint exists only on the futures host. The spot account endpoint
 * ({@code /api/v1/accounts}) returns 404 there, and this one returns 404 on the
 * spot host, so an exchange must have its base URI pointed at the futures host
 * before this can be called.
 *
 * <p>Note that this response and the {@code /contractAccount/wallet} websocket
 * channel name the same quantities differently: {@code marginBalance} /
 * {@code frozenFunds} / {@code accountEquity} here against {@code walletBalance} /
 * {@code holdBalance} / {@code equity} on the channel. Anything consuming both
 * needs to map them separately.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesAccountOverviewResponse {

  /** Currency of this wallet, e.g. USDT. */
  private String currency;

  /** Margin balance plus unrealised profit and loss. */
  private BigDecimal accountEquity;

  /**
   * Margin balance.
   *
   * <p>Believed to be the equivalent of the websocket channel's
   * {@code walletBalance}, but not confirmed: on an account with no open position
   * this field, {@code accountEquity}, {@code availableBalance},
   * {@code availableMargin} and {@code maxWithdrawAmount} all carry the same value,
   * so such a reading cannot distinguish them. Confirming the correspondence needs
   * an account with an open position, where unrealised profit and committed margin
   * are non-zero.
   */
  private BigDecimal marginBalance;

  /** Balance available to use. Named identically on the websocket channel. */
  private BigDecimal availableBalance;

  /** Margin available to use. */
  private BigDecimal availableMargin;

  /** Funds on hold. The websocket channel names this {@code holdBalance}. */
  private BigDecimal frozenFunds;

  /** Maximum amount currently withdrawable. */
  private BigDecimal maxWithdrawAmount;

  /** Margin committed to open orders. */
  private BigDecimal orderMargin;

  /** Margin committed to open positions. */
  private BigDecimal positionMargin;

  /** Unrealised profit and loss on open positions. */
  private BigDecimal unrealisedPNL;

  /** Risk ratio. */
  private BigDecimal riskRatio;
}
