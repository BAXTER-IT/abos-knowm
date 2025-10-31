package org.knowm.xchange.dto.trade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.enums.MarketParticipant;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;

/** Data object representing a user trade */
@Jacksonized
@SuperBuilder
@RequiredArgsConstructor
public class UserTrade extends Trade {

  private static final long serialVersionUID = -3021617981214969292L;

  /** The id of the order responsible for execution of this trade */
  private final String orderId;

  /** The fee that was charged by the exchange for this trade. */
  private final BigDecimal feeAmount;

  /** The currency in which the fee was charged. */
  private final Currency feeCurrency;

  /** The order reference id which has been added by the user on the order creation */
  private final String orderUserReference;

  /** Maker or taker. */
  private final MarketParticipant marketParticipant;

  /**
   * This constructor is called to construct user's trade objects (in {@link
   * TradeService#getTradeHistory(TradeHistoryParams)} implementations).
   *
   * @param type The trade type (BID side or ASK side)
   * @param originalAmount The depth of this trade
   * @param instrument The exchange identifier (e.g. "BTC/USD")
   * @param price The price (either the bid or the ask)
   * @param timestamp The timestamp of the trade
   * @param id The id of the trade
   * @param orderId The id of the order responsible for execution of this trade
   * @param feeAmount The fee that was charged by the exchange for this trade
   * @param feeCurrency The symbol of the currency in which the fee was charged
   * @param orderUserReference The id that the user has insert to the trade
   * @param marketParticipant The maker or taker side
   */
  public UserTrade(
      OrderType type,
      BigDecimal originalAmount,
      Instrument instrument,
      BigDecimal price,
      Date timestamp,
      String id,
      String orderId,
      BigDecimal feeAmount,
      Currency feeCurrency,
      String orderUserReference,
      MarketParticipant marketParticipant,
      String rawJson) {

    super(type, originalAmount, instrument, price, timestamp, id, null, null, rawJson);

    this.orderId = orderId;
    this.feeAmount = feeAmount;
    this.feeCurrency = feeCurrency;
    this.orderUserReference = orderUserReference;
    this.marketParticipant = marketParticipant;
  }

  public String getOrderId() {
    return orderId;
  }

  public BigDecimal getFeeAmount() {
    return feeAmount;
  }

  public Currency getFeeCurrency() {
    return feeCurrency;
  }

  public String getOrderUserReference() {
    return orderUserReference;
  }

  public MarketParticipant getMarketParticipant() {
    return marketParticipant;
  }

  @Override
  public String toString() {
    return "UserTrade{" +
        "orderId='" + orderId + '\'' +
        ", feeAmount=" + feeAmount +
        ", feeCurrency=" + feeCurrency +
        ", orderUserReference='" + orderUserReference + '\'' +
        ", marketParticipant=" + marketParticipant +
        ", type=" + type +
        ", originalAmount=" + originalAmount +
        ", instrument=" + instrument +
        ", price=" + price +
        ", timestamp=" + timestamp +
        ", id='" + id + '\'' +
        ", makerOrderId='" + makerOrderId + '\'' +
        ", takerOrderId='" + takerOrderId + '\'' +
        ", rawJson='" + rawJson + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    UserTrade userTrade = (UserTrade) o;
    return Objects.equals(orderId, userTrade.orderId)
        && Objects.equals(feeAmount, userTrade.feeAmount)
        && Objects.equals(feeCurrency, userTrade.feeCurrency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), orderId, feeAmount, feeCurrency);
  }

}
