package org.knowm.xchange.coincall.dtos.marketdata;

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
public class CoincallSpotInstrumentDto {

  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("baseCoin")
  private String baseCoin;

  @JsonProperty("quoteCoin")
  private String quoteCoin;

  @JsonProperty("enableTrading")
  private boolean enableTrading;

  /**
   * Quantity precision and limits.
   */
  @JsonProperty("lotSizeFilter")
  private CoincallLotSizeFilter lotSizeFilter;

  /**
   * Price tick and precision.
   */
  @JsonProperty("priceFilter")
  private CoincallPriceFilter priceFilter;
}
