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
import org.knowm.xchange.thalex.dto.enums.ThalexWithdrawalState;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class ThalexWithdrawal {

  /**
   * Withdrawn currency symbol.
   */
  @JsonProperty("currency")
  private Currency currency;

  /**
   * Amount of currency withdrawn.
   */
  @JsonProperty("amount")
  private BigDecimal amount;

  /**
   * Target address, specific to blockchain used.
   */
  @JsonProperty("target_address")
  private String targetAddress;

  /**
   * Blockchain used or this transaction.
   */
  @JsonProperty("blockchain")
  private String blockchain;

  /**
   * Transaction hash on the used blockchain.
   */
  @JsonProperty("transaction_hash")
  private String transactionHash;

  /**
   * Time when this withdrawal was requested (UNIX timestamp).
   */
  @JsonProperty("create_time")
  private Instant createTime;

  /**
   * Optional label attached to the withdrawal request.
   */
  @JsonProperty("label")
  private String label;

  /**
   * Withdrawal transaction status.
   */
  @JsonProperty("state")
  private ThalexWithdrawalState state;

  /**
   * Remark added by the exchange.
   */
  @JsonProperty("remark")
  private String remark;

  /**
   * Amount of fees withheld.
   */
  @JsonProperty("fee")
  private BigDecimal fee;

  /**
   * Asset in which the withdrawal fees are withheld.
   */
  @JsonProperty("fee_asset")
  private String feeAsset;
}
