package org.knowm.xchange.thalex.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.thalex.config.deserialize.ThalexTransactionDtoDeserializer;
import org.knowm.xchange.thalex.dto.enums.ThalexTransactionType;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@JsonDeserialize(using = ThalexTransactionDtoDeserializer.class)
public class ThalexTransactionDto {

  @JsonProperty("asset")
  private Currency asset;

  @JsonProperty("time")
  private Instant time;

  /**
   * May be positive or negative.
   */
  @JsonProperty("amount")
  private BigDecimal amount;

  /**
   * Only if this transaction relates to an instrument.
   */
  @JsonProperty("instrument_name")
  private String instrumentName;

  @JsonProperty("transaction_type")
  private ThalexTransactionType transactionType;

  @JsonProperty("description")
  private String description;

  /**
   * Account balance in this currency right after transaction.
   */
  @JsonProperty("balance_after")
  private BigDecimal balanceAfter;

  private String rawJson;
}
