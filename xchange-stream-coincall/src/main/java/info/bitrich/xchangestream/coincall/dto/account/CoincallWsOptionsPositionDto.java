package info.bitrich.xchangestream.coincall.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.coincall.dto.enums.CoincallOptionsPositionStatus;
import info.bitrich.xchangestream.coincall.dto.enums.CoincallPositionSide;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoincallWsOptionsPositionDto {

  @JsonProperty("ap")
  BigDecimal averagePrice;

  @JsonProperty("elp")
  BigDecimal estimatedLiquidationPrice;

  @JsonProperty("im")
  BigDecimal initialMargin;

  @JsonProperty("mm")
  BigDecimal maintenanceMargin;

  @JsonProperty("mp")
  BigDecimal markPrice;

  @JsonProperty("os")
  CoincallOptionsPositionStatus positionStatus;

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
  BigDecimal unrealisedPnlLastPrice;

  @JsonProperty("pnlbmp")
  BigDecimal unrealisedPnlMarkPrice;

  @JsonProperty("roibmp")
  BigDecimal returnOnInvestmentMarkPrice;

  @JsonProperty("rho")
  BigDecimal rho;

  @JsonProperty("vega")
  BigDecimal vega;

  @JsonProperty("delta")
  BigDecimal delta;

  @JsonProperty("gamma")
  BigDecimal gamma;

  @JsonProperty("theta")
  BigDecimal theta;
}
