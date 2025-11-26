package info.bitrich.xchangestream.gateio;

import info.bitrich.xchangestream.gateio.dto.response.PositionPayload;
import info.bitrich.xchangestream.gateio.dto.response.balance.ChangeType;
import info.bitrich.xchangestream.gateio.dto.response.balance.GateioFuturesBalancePayload;
import info.bitrich.xchangestream.gateio.dto.response.balance.GateioSpotBalancePayload;
import info.bitrich.xchangestream.gateio.dto.response.orderbook.GateioOrderBookNotification;
import info.bitrich.xchangestream.gateio.dto.response.orderbook.OrderBookPayload;
import info.bitrich.xchangestream.gateio.dto.response.ticker.GateioTickerNotification;
import info.bitrich.xchangestream.gateio.dto.response.ticker.TickerPayload;
import info.bitrich.xchangestream.gateio.dto.response.trade.GateioTradeNotification;
import info.bitrich.xchangestream.gateio.dto.response.trade.TradePayload;
import info.bitrich.xchangestream.gateio.dto.response.usertrade.GateioSingleUserTradeNotification;
import info.bitrich.xchangestream.gateio.dto.response.usertrade.UserTradePayload;
import java.math.BigDecimal;
import java.util.Date;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Status;
import org.knowm.xchange.dto.account.FundingRecord.Type;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.gateio.GateioAdapters;

@UtilityClass
public class GateioStreamingAdapters {

  public Ticker toTicker(GateioTickerNotification notification) {
    TickerPayload tickerPayload = notification.getResult();

    return new Ticker.Builder()
        .timestamp(Date.from(notification.getTimeMs()))
        .instrument(tickerPayload.getCurrencyPair())
        .last(tickerPayload.getLastPrice())
        .ask(tickerPayload.getLowestAsk())
        .bid(tickerPayload.getHighestBid())
        .percentageChange(tickerPayload.getChangePercent24h())
        .volume(tickerPayload.getBaseVolume())
        .quoteVolume(tickerPayload.getQuoteVolume())
        .high(tickerPayload.getHighPrice24h())
        .low(tickerPayload.getLowPrice24h())
        .build();
  }

  public Trade toTrade(GateioTradeNotification notification) {
    TradePayload tradePayload = notification.getResult();

    return new Trade.Builder()
        .type(tradePayload.getSide())
        .originalAmount(tradePayload.getAmount())
        .instrument(tradePayload.getCurrencyPair())
        .price(tradePayload.getPrice())
        .timestamp(Date.from(tradePayload.getTimeMs()))
        .id(String.valueOf(tradePayload.getId()))
        .build();
  }

  public UserTrade toUserTrade(GateioSingleUserTradeNotification notification) {
    UserTradePayload userTradePayload = notification.getResult();

    return new UserTrade.Builder()
        .type(userTradePayload.getSide())
        .originalAmount(userTradePayload.getAmount())
        .instrument(userTradePayload.getCurrencyPair())
        .price(userTradePayload.getPrice())
        .timestamp(Date.from(userTradePayload.getTimeMs()))
        .id(String.valueOf(userTradePayload.getId()))
        .orderId(String.valueOf(userTradePayload.getOrderId()))
        .feeAmount(userTradePayload.getFee())
        .feeCurrency(userTradePayload.getFeeCurrency())
        .orderUserReference(userTradePayload.getRemark())
        .build();
  }

//  public Balance toBalance(GateioSingleSpotBalanceNotification notification) {
//    GateioSpotBalancePayload balancePayload = notification.getResult();
//
//    return new Balance.Builder()
//        .currency(balancePayload.getCurrency())
//        .total(balancePayload.getTotal())
//        .available(balancePayload.getAvailable())
//        .frozen(balancePayload.getFreeze())
//        .timestamp(Date.from(balancePayload.getTimestampMs()))
//        .build();
//  }

  public OrderBook toOrderBook(GateioOrderBookNotification notification) {
    OrderBookPayload orderBookPayload = notification.getResult();

    Stream<LimitOrder> asks = orderBookPayload.getAsks().stream()
        .map(priceSizeEntry -> new LimitOrder(OrderType.ASK, priceSizeEntry.getSize(),
            orderBookPayload.getCurrencyPair(), null, null, priceSizeEntry.getPrice()));

    Stream<LimitOrder> bids = orderBookPayload.getAsks().stream()
        .map(priceSizeEntry -> new LimitOrder(OrderType.BID, priceSizeEntry.getSize(),
            orderBookPayload.getCurrencyPair(), null, null, priceSizeEntry.getPrice()));

    return new OrderBook(Date.from(orderBookPayload.getTimestamp()), asks, bids);
  }

  public static OpenPosition toOpenPosition(PositionPayload in) {
    if (in == null) {
      return null;
    }

    return new OpenPosition.Builder()
        .instrument(GateioAdapters.toFuturesContract(in.getContract()))
        .type(null) // side/direction is not present in this payload
        .size(in.getSize())
        .price(in.getEntryPrice())
        .liquidationPrice(in.getLiquidationPrice())
        .unRealisedPnl(in.getRealisedPnl())
        .build();
  }

  public static FundingRecord toBalance(GateioSpotBalancePayload in) {
    if (in == null) {
      return null;
    }

    BigDecimal change = in.getChange();
    BigDecimal absChange = change == null ? null : change.abs();
    boolean outflow = change != null && change.signum() < 0;

    Type type = mapType(in.getChangeType(), outflow);

    BigDecimal amount = null;
    BigDecimal fee = null;

    if (in.getChangeType() == ChangeType.TRADE_FEE_DEDUCT || in.getChangeType() == ChangeType.FEE) {
      fee = absChange;
    } else {
      amount = absChange;
    }

    return FundingRecord.builder()
        .date(extractDate(in))
        .currency(in.getCurrency())
        .amount(amount)
        .type(type)
        .status(Status.COMPLETE)
        .balance(in.getTotal())
        .fee(fee)
        .description(buildSpotDescription(in))
        .build();
  }

  public static FundingRecord toBalance(GateioFuturesBalancePayload in) {
    if (in == null) {
      return null;
    }

    BigDecimal change = in.getChange();
    boolean outflow = change != null && change.signum() < 0;

    Type type = mapType(in.getType(), outflow);

    return FundingRecord.builder()
        .date(extractDate(in))
        .currency(Currency.getInstance(in.getText().split("_")[0]))
        .amount(in.getBalance().abs())
        .type(type)
        .status(Status.COMPLETE)
        .balance(in.getBalance())
        .description(buildFuturesDescription(in))
        .build();
  }

  private static Date extractDate(GateioSpotBalancePayload in) {
    if (in.getTimestampMs() != null) {
      return Date.from(in.getTimestampMs());
    }
    if (in.getTimestamp() != null) {
      return Date.from(in.getTimestamp());
    }
    return null;
  }

  private static Date extractDate(GateioFuturesBalancePayload in) {
    if (in.getTimeMs() != null) {
      return Date.from(in.getTimeMs());
    }
    if (in.getTime() != null) {
      return Date.from(in.getTime());
    }
    return null;
  }

  private static Type mapType(ChangeType changeType, boolean outflow) {
    if (changeType == null) {
      return outflow ? Type.OTHER_OUTFLOW : Type.OTHER_INFLOW;
    }

    switch (changeType) {
      case DEPOSIT:
        return Type.DEPOSIT;
      case WITHDRAW:
        return Type.WITHDRAWAL;
      case FEE:
      case TRADE_FEE_DEDUCT:
        return Type.FEE;
      case MARGIN_TRANSFER:
      case FUTURE_TRANSFER:
      case CROSS_MARGIN_TRANSFER:
      case SPOT_TRANSFER:
        return Type.INTERNAL_WALLET_TRANSFER;
      case SUB_TRANSFER:
        return Type.INTERNAL_SUB_ACCOUNT_TRANSFER;
      case REFERRAL_FEE:
        return Type.OTHER_INFLOW;
      case ORDER_CREATE:
      case ORDER_MATCH:
      case ORDER_UPDATE:
      case OTHER:
      default:
        return outflow ? Type.OTHER_OUTFLOW : Type.OTHER_INFLOW;
    }
  }

  private static String buildSpotDescription(GateioSpotBalancePayload in) {
    StringBuilder sb = new StringBuilder("Gate.io spot balance change: ");
    sb.append("type=").append(in.getChangeType());
    sb.append(", currency=").append(in.getCurrency());
    if (in.getChange() != null) {
      sb.append(", change=").append(in.getChange());
    }
    if (in.getTotal() != null) {
      sb.append(", total=").append(in.getTotal());
    }
    return sb.toString();
  }

  private static String buildFuturesDescription(GateioFuturesBalancePayload in) {
    StringBuilder sb = new StringBuilder("Gate.io futures balance change: ");
    sb.append("type=").append(in.getType());
    sb.append(", currency=").append(in.getCurrency());
    if (in.getChange() != null) {
      sb.append(", change=").append(in.getChange());
    }
    if (in.getBalance() != null) {
      sb.append(", balance=").append(in.getBalance());
    }
    if (in.getText() != null) {
      sb.append(", text=").append(in.getText());
    }
    return sb.toString();
  }
}
