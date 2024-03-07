package org.knowm.xchange.finerymarkets.dto.trade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.knowm.xchange.finerymarkets.dto.CancelReason;
import org.knowm.xchange.finerymarkets.dto.OrderType;
import org.knowm.xchange.finerymarkets.dto.Side;
import org.knowm.xchange.finerymarkets.utils.DealHistoryDeserializer;

@Getter
@Builder
@ToString
@JsonDeserialize(using = DealHistoryDeserializer.class)
public class DealHistory {

  private String instrumentName;

  /**
   * Order Type
   *
   * <ul>
   *   <li>0 - limit
   *   <li>1 - post only
   *   <li>2 - limit IOC
   *   <li>3 - limit FOK
   *   <li>4 - market IOC
   *   <li>5 - market FOK
   *   <li>6 - manual trade
   *   <li>7 - liquidation trade
   * </ul>
   */
  private OrderType orderType;

  /**
   * Side
   *
   * <ul>
   *   <li>0 - bid
   *   <li>1 - ask
   * </ul>
   */
  private Side side;

  /**
   * Cancel reason
   *
   * <ul>
   *   <li>0 - in place or filled
   *   <li>1 - by client
   *   <li>2 - as non-book order
   *   <li>3 - by self-trade prevention
   *   <li>4 - cancel-on-disconnect
   * </ul>
   */
  private CancelReason cancelReason;

  private long orderId;

  private long clientOrderId;

  private long orderPrice;

  /** Order Initial Size Or Volume (depending on whether order was initiated by volume) */
  private long orderInitialSize;

  /**
   * Remaining Order Size Or Volume after deal (depending on whether order was initiated by volume)
   */
  private long remainingOrderSize;

  private long orderCreatedAt;

  private long dealMoment;

  private long dealId;

  /**
   * Deal aggressor side
   *
   * <ul>
   *   <li>0 - bid
   *   <li>1 - ask
   * </ul>
   */
  private Side dealAggressorSide;

  private long dealPrice;

  private long dealSize;

  private long dealVolume;

  /** Deal delta in quote (balance) currency */
  private long dealDelta;

  /** Counterparty id */
  private int counterpartyId;

  /**
   * If order was created by size or by volume
   *
   * <ul>
   *   <li>0 - by size
   *   <li>1 - by volume
   * </ul>
   */
  private OrderCreatedBy bySizeOrVolume;

  /** Сounterparty Subaccount Id */
  private int counterpartySubaccountId;

  /**
   * Deal ID current deal is linked to. Deals can be linked when trade happens through the master's
   * account.
   */
  private long linkedDealId;

  private String rawData;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DealHistory that = (DealHistory) o;
    return orderId == that.orderId && orderCreatedAt == that.orderCreatedAt
        && dealMoment == that.dealMoment && dealId == that.dealId && dealPrice == that.dealPrice
        && dealSize == that.dealSize && Objects.equals(instrumentName, that.instrumentName)
        && orderType == that.orderType && side == that.side;
  }

  @Override
  public int hashCode() {
    return Objects.hash(instrumentName, orderType, side, orderId, orderCreatedAt, dealMoment,
        dealId, dealPrice, dealSize);
  }
}
