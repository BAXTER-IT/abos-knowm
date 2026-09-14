package info.bitrich.xchangestream.coincall.dto.trade;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.coincall.dto.enums.CoincallMakerTaker;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.coincall.dtos.enums.CoincallTradeSide;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoincallWsTradeDto {

  /**
   * Symbol, e.g. BTCUSDT.
   */
  @JsonProperty("symbol")
  String symbol;

  /**
   * Client order ID.
   */
  @JsonProperty("clientOrderId")
  String clientOrderId;

  /**
   * Fee rate applied to the trade.
   */
  @JsonProperty("feeRate")
  BigDecimal feeRate;

  /**
   * Trade fee amount.
   */
  @JsonProperty("tradeFee")
  BigDecimal tradeFee;

  /**
   * Maker/taker flag.
   */
  @JsonProperty("isTaker")
  CoincallMakerTaker makerTaker;

  /**
   * Matched price.
   */
  @JsonProperty("price")
  BigDecimal price;

  /**
   * Matched quantity.
   */
  @JsonProperty("volume")
  BigDecimal volume;

  /**
   * Matched notional volume.
   */
  @JsonProperty("matchVolume")
  BigDecimal matchVolume;

  /**
   * Order ID.
   */
  @JsonProperty("orderId")
  Long orderId;

  /**
   * Trade side.
   */
  @JsonProperty("tradeSide")
  CoincallTradeSide tradeSide;

  /**
   * Remaining order volume after this trade.
   */
  @JsonProperty("remainVolume")
  BigDecimal remainingVolume;

  /**
   * User ID.
   */
  @JsonProperty("userId")
  Long userId;

  /**
   * Trade time, Unix timestamp in milliseconds.
   */
  @JsonProperty("ts")
  Long timestamp;

  /**
   * Trade ID.
   */
  @JsonProperty("tradeId")
  String tradeId;
}