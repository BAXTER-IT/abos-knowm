package org.knowm.xchange.gemini.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;
import org.knowm.xchange.gemini.dto.enums.GeminiInstrumentType;

@Data
public class GeminiPositionsResponse {

  private String symbol;

  @JsonProperty("instrument_type")
  private GeminiInstrumentType instrumentType;

  private BigDecimal quantity;

  @JsonProperty("notional_value")
  private BigDecimal notionalValue;

  @JsonProperty("realised_pnl")
  private BigDecimal realisedPnl;

  @JsonProperty("unrealised_pnl")
  private BigDecimal unrealisedPnl;

  @JsonProperty("average_cost")
  private BigDecimal averageCost;

  @JsonProperty("mark_price")
  private BigDecimal markPrice;

}
