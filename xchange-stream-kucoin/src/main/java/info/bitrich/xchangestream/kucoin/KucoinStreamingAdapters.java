package info.bitrich.xchangestream.kucoin;

import info.bitrich.xchangestream.kucoin.dto.KucoinOrderEventData;
import info.bitrich.xchangestream.kucoin.dto.KucoinWebSocketOrderEvent;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsFuturesBalanceData;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsPositionsData;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsSpotBalanceData;
import info.bitrich.xchangestream.kucoin.dto.enums.KucoinRelationEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.derivative.FuturesContract;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Status;
import org.knowm.xchange.dto.account.FundingRecord.Type;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.instrument.Instrument;

public class KucoinStreamingAdapters {

  public static Order adaptOrder(KucoinWebSocketOrderEvent orderEvent) {
    KucoinOrderEventData data = orderEvent.data;

    Order.OrderType orderType = "buy".equals(data.side) ? Order.OrderType.BID : Order.OrderType.ASK;
    CurrencyPair currencyPair = data.getCurrencyPair();

    Order.Builder orderBuilder =
        "market".equals(data.orderType) ? new MarketOrder.Builder(orderType, currencyPair) :
            new LimitOrder.Builder(orderType, currencyPair).limitPrice(new BigDecimal(data.price));

    orderBuilder
        .id(data.orderId)
        .originalAmount(new BigDecimal(data.size))
        .timestamp(new Date(TimeUnit.NANOSECONDS.toMillis(data.orderTime)))
        .cumulativeAmount(new BigDecimal(data.filledSize))
        .orderStatus(adaptStatus(data.status))
    ;

    return orderBuilder.build();
  }

  private static Order.OrderStatus adaptStatus(String status) {
    if ("open".equals(status)) {
      return Order.OrderStatus.NEW;
    }
    if ("match".equals(status)) {
      return Order.OrderStatus.PARTIALLY_FILLED;
    }
    if ("done".equals(status)) {
      return Order.OrderStatus.FILLED;
    }
    return null;
  }

  public static OpenPosition adaptOpenPosition(KucoinWsPositionsData in) {
    if (in == null) {
      return null;
    }

    return OpenPosition.builder()
        .instrument(KucoinStreamingAdapters.adaptFuturesSymbol(in.getSymbol()))
        .type(adaptType(in))
        .size(in.getCurrentQuantity() == null ? null : in.getCurrentQuantity().abs())
        .price(in.getAverageEntryPrice())
        .liquidationPrice(in.getLiquidationPrice())
        .unRealisedPnl(in.getUnrealisedProfitAndLoss())
        .build();
  }

  private static OpenPosition.Type adaptType(KucoinWsPositionsData in) {
    if (in.getCurrentQuantity() == null) {
      return null;
    }

    int cmp = in.getCurrentQuantity().compareTo(java.math.BigDecimal.ZERO);
    if (cmp > 0) {
      return OpenPosition.Type.LONG;
    } else if (cmp < 0) {
      return OpenPosition.Type.SHORT;
    } else {
      return null;
    }
  }

  public static Instrument adaptFuturesSymbol(String symbol) {
    if (symbol == null || symbol.isEmpty()) {
      throw new IllegalArgumentException("Symbol must not be null or empty");
    }

    if (!symbol.endsWith("M")) {
      throw new IllegalArgumentException("Unrecognized KuCoin futures symbol: " + symbol);
    }

    // strip trailing M
    String core = symbol.substring(0, symbol.length() - 1);

    String[] quotes = {"USDT", "USDC", "USD"};

    for (String q : quotes) {
      if (core.endsWith(q)) {
        String base = core.substring(0, core.length() - q.length());
        CurrencyPair pair = new CurrencyPair(base, q);
        return new FuturesContract(pair, symbol);
      }
    }

    throw new IllegalArgumentException(
        "Cannot determine base/quote for KuCoin futures symbol: " + symbol);
  }

  public static FundingRecord adaptFundingRecord(KucoinWsSpotBalanceData in) {
    if (in == null) {
      return null;
    }

    BigDecimal availableChange =
        in.getAvailableChange() != null ? in.getAvailableChange() : BigDecimal.ZERO;
    BigDecimal holdChange =
        in.getHoldChange() != null ? in.getHoldChange() : BigDecimal.ZERO;

    BigDecimal totalChange = availableChange.add(holdChange);
    int sign = totalChange.signum();
    boolean outflow = sign < 0;
    BigDecimal amount = sign == 0 ? null : totalChange.abs();

    Type type = mapSpotType(in.getRelationEvent(), outflow);

    return FundingRecord.builder()
        .currency(in.getCurrency())
        .balance(in.getAvailable())
        .amount(amount)
        .date(new Date(in.getTime()))
        .internalId(in.getRelationEventId())
        .status(Status.COMPLETE)
        .type(type)
        .build();
  }

  public static FundingRecord adaptFundingRecord(KucoinWsFuturesBalanceData in) {
    if (in == null) {
      return null;
    }

    return FundingRecord.builder()
        .date(new Date(in.getTimestamp()))
        .currency(in.getCurrency())
        .blockchainTransactionHash(null)
        .type(Type.OTHER_INFLOW)
        .status(Status.COMPLETE)
        .balance(in.getAvailableBalance())
        .build();
  }

  private static Type mapSpotType(KucoinRelationEvent event, boolean outflow) {
    if (event == null) {
      return outflow ? Type.OTHER_OUTFLOW : Type.OTHER_INFLOW;
    }

    switch (event) {
      // main account
      case MAIN_DEPOSIT:
        return Type.DEPOSIT;
      case MAIN_WITHDRAW_HOLD:
      case MAIN_WITHDRAW_DONE:
        return Type.WITHDRAWAL;
      case MAIN_TRANSFER:
        return Type.INTERNAL_SUB_ACCOUNT_TRANSFER;
      case TRADE_TRANSFER:
      case TRADE_HF_TRANSFER:
      case MARGIN_TRANSFER:
      case MARGIN_V2_TRANSFER:
      case ISOLATED_TRANSFER:
      case ISOLATED_V2_TRANSFER:
        return Type.INTERNAL_WALLET_TRANSFER;

      case MAIN_OTHER:
      case TRADE_HOLD:
      case TRADE_SETTED:
      case TRADE_OTHER:
      case TRADE_HF_HOLD:
      case TRADE_HF_SETTED:
      case TRADE_HF_OTHER:
      case MARGIN_HOLD:
      case MARGIN_SETTED:
      case MARGIN_OTHER:
      case ISOLATED_HOLD:
      case ISOLATED_SETTED:
      case ISOLATED_OTHER:
      case MARGIN_V2_HOLD:
      case MARGIN_V2_SETTED:
      case MARGIN_V2_OTHER:
      case ISOLATED_V2_HOLD:
      case ISOLATED_V2_SETTED:
      case ISOLATED_V2_OTHER:
      case OTHER:
      default:
        return outflow ? Type.OTHER_OUTFLOW : Type.OTHER_INFLOW;
    }
  }
}
