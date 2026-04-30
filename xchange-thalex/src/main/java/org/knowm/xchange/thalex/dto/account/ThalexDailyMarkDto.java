package org.knowm.xchange.thalex.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for a single entry inside the "daily_marks" array returned by
 * private/daily_mark_history (the daily mark-to-market / settlement record).
 *
 * Exactly matches the JSON shape you showed:
 * {
 *   "time": 1774944000.0117314,
 *   "instrument_name": "ETH-PERPETUAL",
 *   "mark_price": 2054.85061184,
 *   "position": 0.01,
 *   "realized_position_pnl": -0.03551134,
 *   "realized_funding_pnl": -0.00530228
 * }
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ThalexDailyMarkDto {

  /**
   * Settlement timestamp (Unix seconds with fractional part).
   * Same type as in ThalexTransactionDto so you can reuse the same deserializer logic if you want.
   */
  @JsonProperty("time")
  private Double time;

  @JsonProperty("instrument_name")
  private String instrumentName;

  @JsonProperty("mark_price")
  private BigDecimal markPrice;

  @JsonProperty("position")
  private BigDecimal position;

  @JsonProperty("realized_position_pnl")
  private BigDecimal realizedPositionPnl;

  @JsonProperty("realized_funding_pnl")
  private BigDecimal realizedFundingPnl;

  /**
   * Raw JSON string of the original response (for debugging/audit, exactly like in ThalexTransactionDto).
   */
  private String rawJson;
}