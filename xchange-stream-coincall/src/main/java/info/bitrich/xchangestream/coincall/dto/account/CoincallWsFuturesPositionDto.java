package info.bitrich.xchangestream.coincall.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.coincall.dto.enums.CoincallContractType;
import info.bitrich.xchangestream.coincall.dto.enums.CoincallPositionSide;
import info.bitrich.xchangestream.coincall.dto.enums.CoincallPositionStatus;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoincallWsFuturesPositionDto {

  @JsonProperty("ap")
  BigDecimal averagePrice;

  @JsonProperty("elp")
  BigDecimal estimatedLiquidationPrice;

  @JsonProperty("im")
  BigDecimal initialMargin;

  @JsonProperty("le")
  BigDecimal leverage;

  @JsonProperty("mm")
  BigDecimal maintenanceMargin;

  @JsonProperty("mp")
  BigDecimal markPrice;

  @JsonProperty("os")
  CoincallPositionStatus positionStatus;

  @JsonProperty("q")
  BigDecimal quantity;

  @JsonProperty("s")
  String symbol;

  @JsonProperty("si")
  CoincallPositionSide side;

  @JsonProperty("tim")
  BigDecimal initialMarginInCurrency;

  @JsonProperty("tmm")
  BigDecimal maintenanceMarginInCurrency;

  @JsonProperty("uid")
  String userId;

  @JsonProperty("upnl")
  BigDecimal unrealisedPnlMarkPrice;

  @JsonProperty("upnlblp")
  BigDecimal unrealisedPnlLastPrice;

  @JsonProperty("roiblp")
  BigDecimal returnOnInvestmentLastPrice;

  @JsonProperty("lp")
  BigDecimal lastPrice;

  @JsonProperty("ctp")
  CoincallContractType contractType;
}
