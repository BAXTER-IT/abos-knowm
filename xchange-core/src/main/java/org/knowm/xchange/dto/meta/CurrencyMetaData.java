package org.knowm.xchange.dto.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
public class CurrencyMetaData implements Serializable {

  private static final long serialVersionUID = -247899067657358542L;

  @JsonProperty("scale")
  private final Integer scale;

  /** Withdrawal fee */
  @JsonProperty("withdrawal_fee")
  private final BigDecimal withdrawalFee;

  /** Minimum withdrawal amount */
  @JsonProperty("min_withdrawal_amount")
  private final BigDecimal minWithdrawalAmount;

  @JsonProperty("raw_json")
  private final String rawJson;
  /** Wallet health */
  @JsonProperty("wallet_health")
  private WalletHealth walletHealth;

  public CurrencyMetaData(Integer scale, BigDecimal withdrawalFee) {
    this(scale, withdrawalFee, null);
  }

  public CurrencyMetaData(Integer scale, BigDecimal withdrawalFee, BigDecimal minWithdrawalAmount) {
    this(scale, withdrawalFee, minWithdrawalAmount, WalletHealth.UNKNOWN);
  }

  public CurrencyMetaData(
      Integer scale,
      BigDecimal withdrawalFee,
      BigDecimal minWithdrawalAmount,
      WalletHealth walletHealth) {
    this(scale, withdrawalFee, minWithdrawalAmount, null, walletHealth);
  }

  public CurrencyMetaData(
      Integer scale,
      BigDecimal withdrawalFee,
      BigDecimal minWithdrawalAmount,
      String rawJson,
      WalletHealth walletHealth) {
    this.scale = scale;
    this.withdrawalFee = withdrawalFee;
    this.minWithdrawalAmount = minWithdrawalAmount;
    this.rawJson = rawJson;
    this.walletHealth = walletHealth;
  }
}
