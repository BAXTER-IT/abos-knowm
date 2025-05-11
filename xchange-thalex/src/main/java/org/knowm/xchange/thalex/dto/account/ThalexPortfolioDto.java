package org.knowm.xchange.thalex.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class ThalexPortfolioDto {

  @JsonProperty("instrument_name")
  private String instrumentName;

  /**
   * Amount of this contract currently held; short positions are negative.
   */
  @JsonProperty("position")
  private BigDecimal position;

  /**
   * Current mark price for the instrument.
   */
  @JsonProperty("mark_price")
  private BigDecimal markPrice;

  /**
   * Implied volatility calculated at time of marking.
   */
  @JsonProperty("iv")
  private BigDecimal impliedVolatility;

  /**
   * Index price at time of marking.
   */
  @JsonProperty("index")
  private BigDecimal index;

  /**
   * Average price paid to obtain position (entry_value / position).
   */
  @JsonProperty("start_price")
  private BigDecimal startPrice;

  /**
   * Average price paid to obtain position. Doesn't reset at settlement.
   */
  @JsonProperty("average_price")
  private BigDecimal averagePrice;

  /**
   * Unrealised P&L for this position based on current mark price
   */
  @JsonProperty("unrealised_pnl")
  private BigDecimal unrealisedPnl;

  /**
   * Realized P&L.
   */
  @JsonProperty("realised_pnl")
  private BigDecimal realisedPnl;

  /**
   * Total entry value. Unrealised positional P&L is (mark price * position) - entry value.
   */
  @JsonProperty("entry_value")
  private BigDecimal entryValue;

  /**
   * Entry mark value for perpetual funding. Unrealised perpetual funding is (current perp funding
   * mark * position) - perpetual funding entry value. Not included if zero.
   */
  @JsonProperty("perpetual_funding_entry_value")
  private BigDecimal perpetualFundingEntryValue;

  /**
   * For perpetual positions, current unrealized perpetual funding.
   */
  @JsonProperty("unrealised_perpetual_funding")
  private BigDecimal unrealisedPerpetualFunding;
}
