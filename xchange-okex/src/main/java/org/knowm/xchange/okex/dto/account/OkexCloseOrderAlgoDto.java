package org.knowm.xchange.okex.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.okex.dto.enums.OkexTriggerPriceType;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OkexCloseOrderAlgoDto {

  /**
   * Algo order ID.
   */
  @JsonProperty("algoId")
  private String algorithmId;

  /**
   * Stop-loss trigger price.
   */
  @JsonProperty("slTriggerPx")
  private BigDecimal stopLossTriggerPrice;

  /**
   * Stop-loss trigger price type.
   */
  @JsonProperty("slTriggerPxType")
  private OkexTriggerPriceType stopLossTriggerPriceType;

  /**
   * Take-profit trigger price.
   */
  @JsonProperty("tpTriggerPx")
  private BigDecimal takeProfitTriggerPrice;

  /**
   * Take-profit trigger price type.
   */
  @JsonProperty("tpTriggerPxType")
  private OkexTriggerPriceType takeProfitTriggerPriceType;

  /**
   * Fraction of position closed when algo triggers.
   */
  @JsonProperty("closeFraction")
  private BigDecimal closeFraction;
}