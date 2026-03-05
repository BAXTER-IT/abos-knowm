package info.bitrich.xchangestream.okex.dto.accont;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.okex.dto.enums.OkexEventType;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.okex.dto.enums.OkexInstrumentType;
import org.knowm.xchange.okex.dto.enums.OkexMarginMode;
import org.knowm.xchange.okex.dto.enums.OkexPositionSide;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OkexWsLedgerDto {


    /** Push time of balance and position information (Unix ms). */
    @JsonProperty("pTime")
    private String pushTime;

    /** Event type (snapshot, delivered, exercised, etc.). */
    @JsonProperty("eventType")
    private OkexEventType eventType;

    /** Balance data. */
    @JsonProperty("balData")
    private List<OkexWsLedgerBalanceDto> balanceData;

    /** Position data. */
    @JsonProperty("posData")
    private List<OkexWsLedgerPositionDto> positionData;

    /** Trade details. */
    @JsonProperty("trades")
    private List<OkexWsLedgerTradeDto> trades;


  /**
   * Ledger balance data for a single currency.
   */
  @Data
  @Value
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OkexWsLedgerBalanceDto {

    /** Currency. */
    @JsonProperty("ccy")
    Currency currency;

    /** Cash balance. */
    @JsonProperty("cashBal")
    BigDecimal cashBalance;

    /** Update time (Unix ms). */
    @JsonProperty("uTime")
    Long updateTime;
  }

  /**
   * Ledger position data for a single instrument.
   */
  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OkexWsLedgerPositionDto {

    /** Position ID. */
    @JsonProperty("posId")
    private String positionId;

    /** Last trade ID affecting this position. */
    @JsonProperty("tradeId")
    private String tradeId;

    /** Instrument ID, e.g. BTC-USD-180213. */
    @JsonProperty("instId")
    private String instrumentId;

    /** Instrument type. */
    @JsonProperty("instType")
    private OkexInstrumentType instrumentType;

    /** Margin mode (isolated, cross). */
    @JsonProperty("mgnMode")
    private OkexMarginMode marginMode;

    /** Average open price. */
    @JsonProperty("avgPx")
    private BigDecimal averageOpenPrice;

    /** Currency used for margin. */
    @JsonProperty("ccy")
    private String marginCurrency;

    /** Position side (long, short, net). */
    @JsonProperty("posSide")
    private OkexPositionSide positionSide;

    /** Quantity of positions. */
    @JsonProperty("pos")
    private BigDecimal positionSize;

    /** Base currency balance (deprecated, Quick Margin Mode). */
    @JsonProperty("baseBal")
    private BigDecimal baseCurrencyBalance;

    /** Quote currency balance (deprecated, Quick Margin Mode). */
    @JsonProperty("quoteBal")
    private BigDecimal quoteCurrencyBalance;

    /** Position currency (for MARGIN positions). */
    @JsonProperty("posCcy")
    private String positionCurrency;

    /** Non-settlement entry price (FUTURES cross). */
    @JsonProperty("nonSettleAvgPx")
    private BigDecimal nonSettlementAveragePrice;

    /** Accumulated settled P&L (FUTURES cross). */
    @JsonProperty("settledPnl")
    private BigDecimal settledProfitAndLoss;

    /** Update time (Unix ms). */
    @JsonProperty("uTime")
    private String updateTime;
  }

  /**
   * Ledger trade detail.
   */
  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OkexWsLedgerTradeDto {

    /** Instrument ID, e.g. BTC-USDT. */
    @JsonProperty("instId")
    private String instrumentId;

    /** Trade ID. */
    @JsonProperty("tradeId")
    private String tradeId;
  }
}