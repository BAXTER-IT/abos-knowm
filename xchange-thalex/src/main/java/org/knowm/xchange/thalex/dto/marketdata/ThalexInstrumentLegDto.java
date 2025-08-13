package org.knowm.xchange.thalex.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ThalexInstrumentLegDto {

  @JsonProperty("instrument_name")
  private final String instrumentName;

  @JsonProperty("quantity")
  private final Integer quantity;
}
