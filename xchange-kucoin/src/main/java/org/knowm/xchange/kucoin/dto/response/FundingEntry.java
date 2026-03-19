package org.knowm.xchange.kucoin.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundingEntry {
  private Long id;
  private String symbol;
  private Long timePoint;
  private BigDecimal fundingRate;
  private BigDecimal markPrice;
  private Integer positionQty;
  private BigDecimal positionCost;
  private BigDecimal funding;
  private String settleCurrency;
  private String context;
  private String marginMode;
}
