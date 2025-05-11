package org.knowm.xchange.thalex.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.thalex.dto.ThalexCash;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class ThalexAccountSummaryDto {

  /**
   * Total unrealised profit or loss.
   */
  @JsonProperty("unrealised_pnl")
  private final BigDecimal unrealisedPnl;

  /**
   * Total margin based on cash holdings.
   */
  @JsonProperty("cash_collateral")
  private final BigDecimal cashCollateral;

  /**
   * Total margin from unrealised P&L and cash holdings.
   */
  @JsonProperty("margin")
  private final BigDecimal margin;

  /**
   * Required margin based on current position.
   */
  @JsonProperty("required_margin")
  private final BigDecimal requiredMargin;

  /**
   * Difference between margin and required margin.
   */
  @JsonProperty("remaining_margin")
  private final BigDecimal remainingMargin;

  /**
   * Realised profit or loss in current session.
   */
  @JsonProperty("session_realised_pnl")
  private final BigDecimal sessionRealisedPnl;

  /**
   * List of cash holdings, for each relevant currency, and how they contribute to margin.
   */
  @JsonProperty("cash")
  private final List<ThalexCash> cash;
}
