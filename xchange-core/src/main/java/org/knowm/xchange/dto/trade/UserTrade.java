package org.knowm.xchange.dto.trade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trade.Builder;
import org.knowm.xchange.enums.MarketParticipant;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;

/** Data object representing a user trade */
@JsonDeserialize(builder = UserTrade.Builder.class)
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

  public static UserTrade.Builder builder() {
    return new UserTrade.Builder();
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
    return "UserTrade[type="
        + type
        + ", originalAmount="
        + originalAmount
        + ", instrument="
        + instrument
        + ", price="
        + price
        + ", timestamp="
        + timestamp
        + ", id="
        + id
        + ", orderId='"
        + orderId
        + '\''
        + ", feeAmount="
        + feeAmount
        + ", feeCurrency='"
        + feeCurrency
        + '\''
        + ", orderUserReference='"
        + orderUserReference
        + '\''
        + ", liquidity='"
        + marketParticipant
        + '\''
        + "]";
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

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder extends Trade.Builder {

    protected String orderId;
    protected BigDecimal feeAmount;
    protected Currency feeCurrency;
    protected String orderUserReference;
    protected MarketParticipant marketParticipant;

    public static Builder from(UserTrade trade) {
      return new Builder()
          .type(trade.getType())
          .originalAmount(trade.getOriginalAmount())
          .instrument(trade.getInstrument())
          .price(trade.getPrice())
          .timestamp(trade.getTimestamp())
          .id(trade.getId())
          .orderId(trade.getOrderId())
          .feeAmount(trade.getFeeAmount())
          .feeCurrency(trade.getFeeCurrency())
          .marketParticipant(trade.getMarketParticipant());
    }

    @Override
    public Builder type(OrderType type) {
      return (Builder) super.type(type);
    }

    @Override
    public Builder originalAmount(BigDecimal originalAmount) {
      return (Builder) super.originalAmount(originalAmount);
    }

    @Override
    public Builder instrument(Instrument instrument) {
      return (Builder) super.instrument(instrument);
    }

    @Override
    public Builder currencyPair(CurrencyPair currencyPair) {
      return (Builder) super.currencyPair(currencyPair);
    }

    @Override
    public Builder price(BigDecimal price) {
      return (Builder) super.price(price);
    }

    @Override
    public Builder timestamp(Date timestamp) {
      return (Builder) super.timestamp(timestamp);
    }

    @Override
    public Builder id(String id) {
      return (Builder) super.id(id);
    }

    @Override
    public Builder makerOrderId(String makerOrderId) {
      return (Builder) super.makerOrderId(makerOrderId);
    }

    @Override
    public Builder takerOrderId(String takerOrderId) {
      return (Builder) super.takerOrderId(takerOrderId);
    }

    @Override
    public Builder rawJson(String rawJson) {
      return (Builder) super.rawJson(rawJson);
    }

    public Builder orderId(String orderId) {
      this.orderId = orderId;
      return this;
    }

    public Builder feeAmount(BigDecimal feeAmount) {
      this.feeAmount = feeAmount;
      return this;
    }

    public Builder feeCurrency(Currency feeCurrency) {
      this.feeCurrency = feeCurrency;
      return this;
    }

    public Builder orderUserReference(String orderUserReference) {
      this.orderUserReference = orderUserReference;
      return this;
    }

    public Builder marketParticipant(MarketParticipant marketParticipant) {
      this.marketParticipant = marketParticipant;
      return this;
    }

    public Builder marketParticipant(String marketParticipant) {
      switch (marketParticipant.toLowerCase().trim()) {
        case "taker":
          this.marketParticipant = MarketParticipant.TAKER;
          break;
        case "maker":
          this.marketParticipant = MarketParticipant.MAKER;
          break;
        default:
          throw new IllegalArgumentException(
              "Not valid market participant provided. " + marketParticipant + " was provided.");
      }
      return this;
    }

    @Override
    public UserTrade build() {
      return new UserTrade(
          type,
          originalAmount,
          instrument,
          price,
          timestamp,
          id,
          orderId,
          feeAmount,
          feeCurrency,
          orderUserReference,
          marketParticipant,
          rawJson);
    }
  }
}
