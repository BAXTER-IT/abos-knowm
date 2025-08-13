package org.knowm.xchange.thalex.dto.trade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.thalex.config.deserialize.ThalexFillDtoDeserializer;
import org.knowm.xchange.thalex.dto.enums.ThalexDirection;
import org.knowm.xchange.thalex.dto.enums.ThalexMakerTaker;
import org.knowm.xchange.thalex.dto.enums.ThalexTradeType;

@Data
@Builder
@JsonDeserialize(using = ThalexFillDtoDeserializer.class)
public class ThalexTradeDto {

  private ThalexTradeType tradeType;

  private String tradeId;

  private String orderId;

  private String instrumentName;

  private ThalexDirection direction;

  private BigDecimal price;

  private BigDecimal amount;

  /**
   * User label.
   */
  private String label;

  /**
   * Time of trade (UNIX timestamp).
   */
  private Instant time;

  /**
   * Position in this instrument right after the trade.
   */
  private BigDecimal positionAfter;

  /**
   * Session realised P&L for this instrument right after the trade.
   */
  private BigDecimal sessionRealisedAfter;

  /**
   * If trade closed a position, the positional P&L that was realised.
   */
  private BigDecimal positionPnl;

  /**
   * If trade closed a position in a perpetual, the funding P&L that was realised.
   */
  private BigDecimal perpetualFundingPnl;

  /**
   * The fee rate applied to calculate the fee.
   */
  private BigDecimal fee;

  /**
   * The relevant index at time of trade.
   */
  private BigDecimal index;

  /**
   * The fee rate applied to calculate the fee.
   */
  private BigDecimal feeRate;

  /**
   * The perpetual funding mark as applied to the trade (see Ticker).
   */
  private BigDecimal fundingMark;

  /**
   * Fee paid in case of liquidation.
   */
  private BigDecimal liquidationFee;

  /**
   * Client order reference as set in related order.
   */
  private String clientOrderId;

  /**
   * Maker (trade on book order) or taker (trade on new order), if applicable.
   */
  private ThalexMakerTaker makerTaker;

  private String rawJson;
}
