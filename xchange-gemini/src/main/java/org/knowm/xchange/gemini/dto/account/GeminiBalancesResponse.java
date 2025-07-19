package org.knowm.xchange.gemini.dto.account;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiBalancesResponse {

  private String type;
  private String currency;
  private BigDecimal amount;
  private BigDecimal available;
  private BigDecimal availableForWithdrawal;
  private BigDecimal pendingWithdrawal;
  private BigDecimal pendingDeposit;

}
