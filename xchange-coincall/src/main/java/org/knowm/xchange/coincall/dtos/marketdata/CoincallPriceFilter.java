package org.knowm.xchange.coincall.dtos.marketdata;

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
public class CoincallPriceFilter {

  /**
   * Minimum price increment (tick size).
   */
  @JsonProperty("tickSize")
  private BigDecimal tickSize;

}
