package org.knowm.xchange.kucoin.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountsResponse {

  private String currency;

  private BigDecimal balance;

  private BigDecimal available;

  private BigDecimal holds;

  private String baseCurrency;

  private BigDecimal baseCurrencyPrice;

  private BigDecimal baseAmount;
}
