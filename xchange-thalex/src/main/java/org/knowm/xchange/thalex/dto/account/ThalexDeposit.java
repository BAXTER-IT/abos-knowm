package org.knowm.xchange.thalex.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.thalex.dto.enums.ThalexDepositStatus;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class ThalexDeposit {

  /**
   * Deposit currency symbol.
   */
  @JsonProperty("currency")
  private Currency currency;

  /**
   * Amount of currency deposited.
   */
  @JsonProperty("amount")
  private BigDecimal amount;

  /**
   * Blockchain used for this transaction.
   */
  @JsonProperty("blockchain")
  private String blockchain;

  /**
   * Transaction hash on the used blockchain.
   */
  @JsonProperty("transaction_hash")
  private String transactionHash;

  /**
   * Time when this transaction was created (UNIX timestamp).
   */
  @JsonProperty("transaction_timestamp")
  private Instant transactionTimestamp;

  /**
   * Deposit transaction status.
   */
  @JsonProperty("status")
  private ThalexDepositStatus status;

  /**
   * Number of confirmations. Optional, omitted when none.
   */
  @JsonProperty("confirmations")
  private Long confirmations;
}
