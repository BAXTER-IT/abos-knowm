package info.bitrich.xchangestream.bybit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.knowm.xchange.bybit.dto.BybitCategory;
import org.knowm.xchange.bybit.dto.trade.BybitExecType;
import org.knowm.xchange.bybit.dto.trade.BybitOrderType;
import org.knowm.xchange.bybit.dto.trade.BybitSide;

@ToString
@Getter
@AllArgsConstructor
public class BybitUserTradeResponseDto {

  @JsonProperty("id")
  private String id;

  @JsonProperty("topic")
  private String topic;

  @JsonProperty("creationTime")
  private Date creationTime;

  @JsonProperty("data")
  private List<BybitUserTradeData> data;

  public BybitUserTradeResponseDto() {
  }

  @Getter
  @ToString
  @AllArgsConstructor
  @Builder
  public static class BybitUserTradeData {

    @JsonProperty("category")
    private  BybitCategory category;

    @JsonProperty("symbol")
    private  String symbol;

    @JsonProperty("isLeverage")
    private  String isLeverage;

    @JsonProperty("orderId")
    private  String orderId;

    @JsonProperty("orderLinkId")
    private  String orderLinkId;

    @JsonProperty("side")
    private  BybitSide side;

    @JsonProperty("orderPrice")
    private  BigDecimal orderPrice;

    @JsonProperty("orderQty")
    private  BigDecimal orderQty;

    @JsonProperty("leavesQty")
    private  BigDecimal leavesQty;

    @JsonProperty("orderType")
    private  BybitOrderType orderType;

    @JsonProperty("stopOrderType")
    private  BybitOrderType stopOrderType;

    @JsonProperty("execFee")
    private  BigDecimal execFee;

    @JsonProperty("execId")
    private  String execId;

    @JsonProperty("execPrice")
    private  BigDecimal execPrice;

    @JsonProperty("execQty")
    private  BigDecimal execQty;

    @JsonProperty("execType")
    private  BybitExecType execType;

    @JsonProperty("execValue")
    private  BigDecimal execValue;

    @JsonProperty("execTime")
    private  Date execTime;

    @JsonProperty("isMaker")
    private  Boolean isMaker;

    @JsonProperty("feeRate")
    private  BigDecimal feeRate;

    @JsonProperty("tradeIv")
    private  BigDecimal tradeIv;

    @JsonProperty("markIv")
    private  BigDecimal markIv;

    @JsonProperty("markPrice")
    private  BigDecimal markPrice;

    @JsonProperty("indexPrice")
    private  BigDecimal indexPrice;

    @JsonProperty("underlyingPrice")
    private  BigDecimal underlyingPrice;

    @JsonProperty("blockTradeId")
    private  String blockTradeId;

    @JsonProperty("closedSize")
    private  BigDecimal closedSize;

    @JsonProperty("seq")
    private  Long seq;

    public BybitUserTradeData() {
    }
  }
}
