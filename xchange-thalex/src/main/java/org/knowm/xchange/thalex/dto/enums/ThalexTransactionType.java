package org.knowm.xchange.thalex.dto.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ThalexTransactionType {
  /**
   * Deposits and asset credits.
   */
  DEPOSIT("deposit"),

  /**
   * Withdrawals and asset debits.
   */
  WITHDRAWAL("withdrawal"),

  /**
   * Withdrawal fees.
   */
  WITHDRAWAL_FEE("withdrawal fee"),

  /**
   * Settled session PNL.
   */
  SESSION_SETTLEMENT("session settlement"),

  /**
   * Settled perpetual funding.
   */
  PERPETUAL_FUNDING("perpetual funding"),

  /**
   * Transfer of assets between subaccounts.
   * One transaction in each subaccount per asset per transfer.
   */
  INTERNAL_TRANSFER("internal transfer"),

  /**
   * Swap between assets.
   * One transaction for each side of the asset pair per swap.
   */
  ASSET_SWAP("asset swap"),

  /**
   * Referral program rewards.
   */
  REFERRAL_PROGRAM_PAYMENT("referral program payment"),

  /**
   * MVP (Market Velocity Program) rewards.
   */
  MARKET_VELOCITY_PROGRAM_PAYMENT("market velocity program payment"),

  /**
   * MQP (Market Quality Program) rewards.
   */
  MARKET_QUALITY_PROGRAM_PAYMENT("market quality program payment"),

  /**
   * Daily penalty charge for negative balance.
   * Not used anymore.
   */
  DAILY_INTEREST("daily interest");

  @JsonValue
  private final String thalexValue;

  @JsonCreator
  public static ThalexTransactionType fromThalexValue(String thalexValue) {
    for (ThalexTransactionType value : ThalexTransactionType.values()) {
      if (value.thalexValue.equals(thalexValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown Thalex transaction type value: " + thalexValue);
  }
}
