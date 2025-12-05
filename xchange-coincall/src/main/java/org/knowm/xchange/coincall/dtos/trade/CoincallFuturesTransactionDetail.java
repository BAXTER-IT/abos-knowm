package org.knowm.xchange.coincall.dtos.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.coincall.dtos.enums.CoincallTradeSide;
import org.knowm.xchange.coincall.dtos.enums.CoincallTradeType;
import org.knowm.xchange.utils.jackson.RawJsonAware;

@Data
@Builder
@Jacksonized
public class CoincallFuturesTransactionDetail implements RawJsonAware {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("contractId")
  private Long contractId;

  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("tradeSide")
  private CoincallTradeSide tradeSide;

  @JsonProperty("tradeType")
  private CoincallTradeType tradeType;

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("qty")
  private BigDecimal qty;

  @JsonProperty("markPrice")
  private BigDecimal markPrice;

  @JsonProperty("indexPrice")
  private BigDecimal indexPrice;

  @JsonProperty("orderId")
  private Long orderId;

  @JsonProperty("tradeId")
  private Long tradeId;

  @JsonProperty("fee")
  private BigDecimal fee;

  @JsonProperty("time")
  private Long time;

  @JsonProperty("leverage")
  private Integer leverage;

  @JsonProperty("isTaker")
  private boolean isTaker;

  private String rawJson;
}
