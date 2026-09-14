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
public class CoincallLotSizeFilter {

  /**
   * Precision of base coin quantities.
   */
  @JsonProperty("basePrecision")
  private int basePrecision;

  /**
   * Precision of quote coin prices.
   */
  @JsonProperty("quotePrecision")
  private int quotePrecision;

  /**
   * Maximum order size.
   */
  @JsonProperty("maxOrderSize")
  private BigDecimal maxOrderSize;

  /**
   * Minimum order quantity.
   */
  @JsonProperty("minQty")
  private BigDecimal minQuantity;

  /**
   * Maximum order quantity.
   */
  @JsonProperty("maxQty")
  private BigDecimal maxQuantity;

  /**
   * Maximum market order quantity.
   */
  @JsonProperty("maxMarketQty")
  private BigDecimal maxMarketQuantity;
}