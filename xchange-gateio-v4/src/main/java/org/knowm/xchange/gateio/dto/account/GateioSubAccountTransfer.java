package org.knowm.xchange.gateio.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.gateio.config.converter.StringToCurrencyConverter;
import org.knowm.xchange.gateio.config.converter.TimestampSecondsToInstantConverter;

@Data
@Builder
@Jacksonized
public class GateioSubAccountTransfer {

  /**
   * Main account user ID
   */
  @JsonProperty("uid")
  Integer mainAccountId;

  @JsonProperty("timest")
  @JsonDeserialize(converter = TimestampSecondsToInstantConverter.class)
  private Instant timestamp;

  /**
   * Where the operation is initiated from, like "web"
   */
  @JsonProperty("source")
  String source;

  @JsonProperty("client_order_id")
  String clientOrderId;

  @JsonProperty("currency")
  @JsonDeserialize(converter = StringToCurrencyConverter.class)
  Currency currency;

  /**
   * Sub account user ID
   */
  @JsonProperty("sub_account")
  Integer subAccountId;

  /**
   * Transfer direction. to - transfer into sub account; from - transfer out from sub account
   */
  @JsonProperty("direction")
  String direction;

  @JsonProperty("amount")
  BigDecimal amount;

  /**
   * Target sub user's account. spot - spot account, futures - perpetual contract account, cross_margin - cross margin account, delivery - delivery account
   */
  @JsonProperty("sub_account_type")
  String subAccountType;

}
