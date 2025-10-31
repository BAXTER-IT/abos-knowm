package org.knowm.xchange.gemini;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.derivative.FuturesContract;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderStatus;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Fee;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Status;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.account.OpenPosition.Type;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.marketdata.Trades.TradeSortType;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.dto.trade.FixedRateLoanOrder;
import org.knowm.xchange.dto.trade.FloatingRateLoanOrder;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.gemini.dto.account.GeminiBalancesResponse;
import org.knowm.xchange.gemini.dto.account.GeminiPositionsResponse;
import org.knowm.xchange.gemini.dto.account.GeminiTrailingVolumeResponse;
import org.knowm.xchange.gemini.dto.account.GeminiTransaction;
import org.knowm.xchange.gemini.dto.account.GeminiTransactionTransferResponse;
import org.knowm.xchange.gemini.dto.account.GeminiTransferResponse;
import org.knowm.xchange.gemini.dto.enums.GeminiTradeType;
import org.knowm.xchange.gemini.dto.marketdata.GeminiDepth;
import org.knowm.xchange.gemini.dto.marketdata.GeminiLendLevel;
import org.knowm.xchange.gemini.dto.marketdata.GeminiLevel;
import org.knowm.xchange.gemini.dto.marketdata.GeminiTicker;
import org.knowm.xchange.gemini.dto.marketdata.GeminiTrade;
import org.knowm.xchange.gemini.dto.trade.GeminiOrderStatusResponse;
import org.knowm.xchange.gemini.dto.trade.GeminiOrderStatusResponse.OrderStatusTradeDetails;
import org.knowm.xchange.gemini.dto.trade.GeminiTradeResponse;
import org.knowm.xchange.gemini.dto.trade.GeminiTransactionTradeResponse;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GeminiAdapters {

  public static final Logger log = LoggerFactory.getLogger(GeminiAdapters.class);

  private GeminiAdapters() {}

  public static List<CurrencyPair> adaptCurrencyPairs(Collection<String> GeminiSymbol) {

    List<CurrencyPair> currencyPairs = new ArrayList<>();
    for (String symbol : GeminiSymbol) {
      currencyPairs.add(adaptCurrencyPair(symbol));
    }
    return currencyPairs;
  }

  public static CurrencyPair adaptCurrencyPair(String symbol) {

    String tradableIdentifier = symbol.substring(0, symbol.length() - 3).toUpperCase();
    String transactionCurrency = symbol.substring(symbol.length() - 3).toUpperCase();
    return new CurrencyPair(tradableIdentifier, transactionCurrency);
  }

  public static String adaptCurrencyPair(CurrencyPair pair) {

    return (pair.getBase().getCurrencyCode() + pair.getCounter().getCurrencyCode()).toLowerCase();
  }

  public static OrderBook adaptOrderBook(GeminiDepth btceDepth, CurrencyPair currencyPair) {

    OrdersContainer asksOrdersContainer =
        adaptOrders(btceDepth.getAsks(), currencyPair, OrderType.ASK);
    OrdersContainer bidsOrdersContainer =
        adaptOrders(btceDepth.getBids(), currencyPair, OrderType.BID);

    return new OrderBook(
        new Date(Math.max(asksOrdersContainer.getTimestamp(), bidsOrdersContainer.getTimestamp())),
        asksOrdersContainer.getLimitOrders(),
        bidsOrdersContainer.getLimitOrders());
  }

  public static OrdersContainer adaptOrders(
      GeminiLevel[] GeminiLevels, CurrencyPair currencyPair, OrderType orderType) {

    BigDecimal maxTimestamp = new BigDecimal(Long.MIN_VALUE);
    List<LimitOrder> limitOrders = new ArrayList<>(GeminiLevels.length);

    for (GeminiLevel GeminiLevel : GeminiLevels) {
      if (GeminiLevel.getTimestamp().compareTo(maxTimestamp) > 0) {
        maxTimestamp = GeminiLevel.getTimestamp();
      }

      Date timestamp = convertBigDecimalTimestampToDate(GeminiLevel.getTimestamp());
      limitOrders.add(
          adaptOrder(
              GeminiLevel.getAmount(), GeminiLevel.getPrice(), currencyPair, orderType, timestamp));
    }

    long maxTimestampInMillis = maxTimestamp.multiply(new BigDecimal(1000L)).longValue();
    return new OrdersContainer(maxTimestampInMillis, limitOrders);
  }

  public static Order adaptOrder(GeminiOrderStatusResponse geminiOrderStatusResponse) {

    Long id = geminiOrderStatusResponse.getId();
    CurrencyPair currencyPair = adaptCurrencyPair(geminiOrderStatusResponse.getSymbol());
    BigDecimal averageExecutionPrice = geminiOrderStatusResponse.getAvgExecutionPrice();
    BigDecimal executedAmount = geminiOrderStatusResponse.getExecutedAmount();
    BigDecimal originalAmount = geminiOrderStatusResponse.getOriginalAmount();
    OrderType orderType =
        (geminiOrderStatusResponse.getSide().equals("buy")) ? OrderType.BID : OrderType.ASK;
    OrderStatus orderStatus = adaptOrderstatus(geminiOrderStatusResponse);
    Date timestamp = new Date(geminiOrderStatusResponse.getTimestampms());

    // Calculate Fees in counter currency
    BigDecimal fee = null;

    if (geminiOrderStatusResponse.getTrades() != null
        && geminiOrderStatusResponse.getTrades().length > 0) {
      for (OrderStatusTradeDetails trade :
          geminiOrderStatusResponse.getTrades()) {
        if (fee == null) {
          fee = trade.getFeeAmount();
        } else {
          fee.add(trade.getFeeAmount());
        }
      }
    }

    if (geminiOrderStatusResponse.getType().contains("limit")) {

      BigDecimal limitPrice = geminiOrderStatusResponse.getPrice();

      return new LimitOrder(
          orderType,
          originalAmount,
          currencyPair,
          id.toString(),
          timestamp,
          limitPrice,
          averageExecutionPrice,
          executedAmount,
          fee,
          orderStatus,
          geminiOrderStatusResponse.getClientOrderId());

    } else if (geminiOrderStatusResponse.getType().contains("market")) {

      return new MarketOrder(
          orderType,
          originalAmount,
          currencyPair,
          id.toString(),
          timestamp,
          averageExecutionPrice,
          executedAmount,
          fee,
          orderStatus,
          geminiOrderStatusResponse.getClientOrderId());
    }

    throw new NotYetImplementedForExchangeException();
  }

  private static OrderStatus adaptOrderstatus(GeminiOrderStatusResponse geminiOrderStatusResponse) {

    if (geminiOrderStatusResponse.isCancelled()) return OrderStatus.CANCELED;

    if (geminiOrderStatusResponse.getExecutedAmount().equals(BigDecimal.ZERO))
      return OrderStatus.OPEN;

    if (geminiOrderStatusResponse.getRemainingAmount().equals(BigDecimal.ZERO))
      return OrderStatus.FILLED;

    if (geminiOrderStatusResponse.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0)
      return OrderStatus.PARTIALLY_FILLED;

    throw new NotYetImplementedForExchangeException();
  }

  public static LimitOrder adaptOrder(
      BigDecimal amount,
      BigDecimal price,
      CurrencyPair currencyPair,
      OrderType orderType,
      Date timestamp) {

    return new LimitOrder(orderType, amount, currencyPair, "", timestamp, price);
  }

  public static List<FixedRateLoanOrder> adaptFixedRateLoanOrders(
      GeminiLendLevel[] orders, String currency, String orderType, String id) {

    List<FixedRateLoanOrder> loanOrders = new ArrayList<>(orders.length);

    for (GeminiLendLevel order : orders) {
      if ("yes".equalsIgnoreCase(order.getFrr())) {
        continue;
      }

      // Bid orderbook is reversed order. Insert at reversed indices
      if (orderType.equalsIgnoreCase("loan")) {
        loanOrders.add(
            0,
            adaptFixedRateLoanOrder(
                currency, order.getAmount(), order.getPeriod(), orderType, id, order.getRate()));
      } else {
        loanOrders.add(
            adaptFixedRateLoanOrder(
                currency, order.getAmount(), order.getPeriod(), orderType, id, order.getRate()));
      }
    }

    return loanOrders;
  }

  public static FixedRateLoanOrder adaptFixedRateLoanOrder(
      String currency,
      BigDecimal amount,
      int dayPeriod,
      String direction,
      String id,
      BigDecimal rate) {

    OrderType orderType = direction.equalsIgnoreCase("loan") ? OrderType.BID : OrderType.ASK;

    return new FixedRateLoanOrder(orderType, currency, amount, dayPeriod, id, null, rate);
  }

  public static List<FloatingRateLoanOrder> adaptFloatingRateLoanOrders(
      GeminiLendLevel[] orders, String currency, String orderType, String id) {

    List<FloatingRateLoanOrder> loanOrders = new ArrayList<>(orders.length);

    for (GeminiLendLevel order : orders) {
      if ("no".equals(order.getFrr())) {
        continue;
      }

      // Bid orderbook is reversed order. Insert at reversed indices
      if (orderType.equalsIgnoreCase("loan")) {
        loanOrders.add(
            0,
            adaptFloatingRateLoanOrder(
                currency, order.getAmount(), order.getPeriod(), orderType, id, order.getRate()));
      } else {
        loanOrders.add(
            adaptFloatingRateLoanOrder(
                currency, order.getAmount(), order.getPeriod(), orderType, id, order.getRate()));
      }
    }

    return loanOrders;
  }

  public static FloatingRateLoanOrder adaptFloatingRateLoanOrder(
      String currency,
      BigDecimal amount,
      int dayPeriod,
      String direction,
      String id,
      BigDecimal rate) {

    OrderType orderType = direction.equalsIgnoreCase("loan") ? OrderType.BID : OrderType.ASK;

    return new FloatingRateLoanOrder(orderType, currency, amount, dayPeriod, id, null, rate);
  }

  public static Trade adaptTrade(GeminiTrade trade, CurrencyPair currencyPair) {

    OrderType orderType = trade.getType().equals("buy") ? OrderType.BID : OrderType.ASK;
    BigDecimal amount = trade.getAmount();
    BigDecimal price = trade.getPrice();
    Date date =
        DateUtils.fromMillisUtc(trade.getTimestamp() * 1000L); // Gemini uses Unix timestamps
    final String tradeId = String.valueOf(trade.getTradeId());
    return UserTrade.builder()
        .type(orderType)
        .originalAmount(amount)
        .instrument(currencyPair)
        .price(price)
        .timestamp(date)
        .id(tradeId)
        .build();
  }

  public static Trades adaptTrades(GeminiTrade[] trades, CurrencyPair currencyPair) {

    List<Trade> tradesList = new ArrayList<>(trades.length);
    long lastTradeId = 0;
    for (GeminiTrade trade : trades) {
      long tradeId = trade.getTradeId();
      if (tradeId > lastTradeId) {
        lastTradeId = tradeId;
      }
      tradesList.add(adaptTrade(trade, currencyPair));
    }
    return new Trades(tradesList, lastTradeId, TradeSortType.SortByID);
  }

  public static Ticker adaptTicker(GeminiTicker GeminiTicker, CurrencyPair currencyPair) {

    BigDecimal last = GeminiTicker.getLast();
    BigDecimal bid = GeminiTicker.getBid();
    BigDecimal ask = GeminiTicker.getAsk();
    BigDecimal volume = GeminiTicker.getVolume().getBaseVolume(currencyPair);

    Date timestamp = DateUtils.fromMillisUtc(GeminiTicker.getVolume().getTimestampMS());

    return new Ticker.Builder()
        .currencyPair(currencyPair)
        .last(last)
        .bid(bid)
        .ask(ask)
        .volume(volume)
        .timestamp(timestamp)
        .build();
  }

  public static Wallet adaptWallet(List<GeminiBalancesResponse> in) {
    List<Balance> balances = in.stream().map(GeminiAdapters::toBalance).collect(Collectors.toList());
    return Wallet.Builder.from(balances).build();
  }

  private static Balance toBalance(GeminiBalancesResponse in) {
    Currency currency = new Currency(in.getCurrency());
    BigDecimal total = in.getAmount() != null ? in.getAmount().stripTrailingZeros() : BigDecimal.ZERO;
    BigDecimal available = in.getAvailable() != null ? in.getAvailable() : BigDecimal.ZERO;
    BigDecimal pendingWithdrawal = in.getPendingWithdrawal() != null ? in.getPendingWithdrawal() : BigDecimal.ZERO;
    BigDecimal pendingDeposit = in.getPendingDeposit() != null ? in.getPendingDeposit() : BigDecimal.ZERO;
    return new Balance.Builder()
        .currency(currency)
        .total(total)
        .available(available)
        .withdrawing(pendingWithdrawal)
        .depositing(pendingDeposit)
        .build();
  }

  public static List<OpenPosition> adaptPositions(List<GeminiPositionsResponse> in) {
    return in.stream().map(GeminiAdapters::toPosition).collect(Collectors.toList());
  }

  private static OpenPosition toPosition(GeminiPositionsResponse in) {
    Instrument instrument = GeminiAdapters.adaptInstrument(in.getSymbol());
    Type type = in.getQuantity().compareTo(BigDecimal.ZERO) >= 0 ? Type.LONG : Type.SHORT;
    return OpenPosition.builder()
        .instrument(instrument)
        .type(type)
        .size(in.getQuantity())
        .price(in.getAverageCost())
        .unRealisedPnl(in.getUnrealisedPnl())
        .build();
  }

  public static Instrument adaptInstrument(String in) {
    String perpString = "PERP";
    if (in.toUpperCase().endsWith(perpString)) {
      String symbol = in.substring(0, in.length() - 4);
      String baseCurrency = symbol.substring(0, symbol.length() - 3);
      String counterCurrency = symbol.substring(symbol.length() - 3);
      CurrencyPair currencyPair = new CurrencyPair(baseCurrency, counterCurrency);
      return new FuturesContract(currencyPair, perpString);
    }
    String baseCurrency = in.substring(0, in.length() - 3);
    String counterCurrency = in.substring(in.length() - 3);
    return new CurrencyPair(baseCurrency, counterCurrency);
  }

  public static OpenOrders adaptOrders(GeminiOrderStatusResponse[] activeOrders) {
    return adaptOrders(activeOrders, null);
  }

  public static OpenOrders adaptOrders(
      GeminiOrderStatusResponse[] activeOrders, CurrencyPair currencyPair) {

    List<LimitOrder> limitOrders = new ArrayList<>(activeOrders.length);

    for (GeminiOrderStatusResponse order : activeOrders) {
      CurrencyPair currentCurrencyPair = adaptCurrencyPair(order.getSymbol());

      if (currencyPair != null && !currentCurrencyPair.equals(currencyPair)) {
        continue;
      }

      OrderType orderType = order.getSide().equalsIgnoreCase("buy") ? OrderType.BID : OrderType.ASK;
      Date timestamp = convertBigDecimalTimestampToDate(new BigDecimal(order.getTimestamp()));

      OrderStatus status = OrderStatus.NEW;

      if (order.isCancelled()) {
        status = OrderStatus.CANCELED;
      } else if (order.getExecutedAmount().signum() > 0
          && order.getExecutedAmount().compareTo(order.getOriginalAmount()) < 0) {
        status = OrderStatus.PARTIALLY_FILLED;
      } else if (order.getExecutedAmount().compareTo(order.getOriginalAmount()) == 0) {
        status = OrderStatus.FILLED;
      }

      LimitOrder limitOrder =
          new LimitOrder(
              orderType,
              order.getOriginalAmount(),
              currentCurrencyPair,
              String.valueOf(order.getId()),
              timestamp,
              order.getPrice(),
              order.getAvgExecutionPrice(),
              order.getExecutedAmount(),
              null,
              status);

      limitOrders.add(limitOrder);
    }

    return new OpenOrders(limitOrders);
  }

  public static UserTrades adaptTradeHistory(List<GeminiTradeResponse> trades) {

    List<UserTrade> pastTrades = new ArrayList<>(trades.size());

    for (GeminiTradeResponse trade : trades) {
      OrderType orderType = GeminiTradeType.BUY.equals(trade.getType()) ? OrderType.BID : OrderType.ASK;
      Date date = new Date(trade.getTimestampms());
      final BigDecimal fee = trade.getFeeAmount();
      pastTrades.add(UserTrade.builder()
              .type(orderType)
              .originalAmount(trade.getAmount())
              .instrument(adaptInstrument(trade.getSymbol()))
              .price(trade.getPrice())
              .timestamp(date)
              .id(String.valueOf(trade.getTradeId()))
              .orderId(trade.getOrderId())
              .orderUserReference(trade.getClientOrderId())
              .feeAmount(fee)
              .feeCurrency(Currency.getInstance(trade.getFeeCurrency()))
              .rawJson(trade.getRawJson())
              .build());
    }

    return new UserTrades(pastTrades, TradeSortType.SortByTimestamp);
  }

  private static Date convertBigDecimalTimestampToDate(BigDecimal timestampInSeconds) {

    return new Date((long) Math.floor(timestampInSeconds.doubleValue() * 1000));
  }

  public static ExchangeMetaData adaptMetaData(
      List<CurrencyPair> currencyPairs, ExchangeMetaData metaData) {

    Map<Instrument, InstrumentMetaData> pairsMap = metaData.getInstruments();
    Map<Currency, CurrencyMetaData> currenciesMap = metaData.getCurrencies();
    for (CurrencyPair c : currencyPairs) {
      if (!pairsMap.containsKey(c)) {
        pairsMap.put(c, null);
      }
      if (!currenciesMap.containsKey(c.getBase())) {
        currenciesMap.put(c.getBase(), null);
      }
      if (!currenciesMap.containsKey(c.getCounter())) {
        currenciesMap.put(c.getCounter(), null);
      }
    }

    return metaData;
  }

  public static Map<Instrument, Fee> AdaptDynamicTradingFees(
      GeminiTrailingVolumeResponse volumeResponse, List<Instrument> currencyPairs) {
    Map<Instrument, Fee> result = new Hashtable<>();
    BigDecimal bpsToFraction =
        BigDecimal.ONE.divide(BigDecimal.ONE.scaleByPowerOfTen(4), 4, RoundingMode.HALF_EVEN);
    Fee feeAcrossCurrencies =
        new Fee(
            volumeResponse.apiMakerFeeBPS.multiply(bpsToFraction),
            volumeResponse.apiTakerFeeBPS.multiply(bpsToFraction));
    for (Instrument currencyPair : currencyPairs) {
      result.put(currencyPair, feeAcrossCurrencies);
    }

    return result;
  }

  public static FundingRecord adaptTransfer(GeminiTransferResponse transfer) {
    Status status = Status.PROCESSING;
    if ("Complete".equals(transfer.getStatus()) || "Advanced".equals(transfer.getStatus())) {
      status = Status.COMPLETE;
    }

    String description = "";
    if (transfer.getPurpose() != null) description = transfer.getPurpose();

    if (transfer.getMethod() != null) description += " " + transfer.getMethod();

    description = description.trim();

    FundingRecord.Type type = null;
    if ("Withdrawal".equals(transfer.getType())) {
      type = FundingRecord.Type.WITHDRAWAL;
    } else if ("Deposit".equals(transfer.getType())) {
      type = FundingRecord.Type.DEPOSIT;
    }

    return FundingRecord.builder()
        .status(status)
        .type(type)
        .internalId(transfer.getEid())
        .address(transfer.getDestination())
        .currency(Currency.getInstance(transfer.getCurrency()))
        .date(DateUtils.fromMillisUtc(transfer.getTimestampms()))
        .amount(transfer.getAmount())
        .blockchainTransactionHash(transfer.getTxnHash())
        .description(description)
        .build();
  }

  public static List<FundingRecord> adaptTransactions(List<GeminiTransaction> in) {
    return in.stream().map(GeminiAdapters::adaptTransaction).filter(Objects::isNull)
        .collect(Collectors.toList());
  }

  private static FundingRecord adaptTransaction(GeminiTransaction in) {
    // TODO commented lines need mapping
    if (in instanceof GeminiTransactionTradeResponse) {
      GeminiTransactionTradeResponse response = (GeminiTransactionTradeResponse) in;
      return FundingRecord.builder()
//          .status(status)
          .type(FundingRecord.Type.TRADE)
          .currency(Currency.getInstance(response.getSymbol()))
          .date(DateUtils.fromMillisUtc(response.getTimestampMs()))
          .amount(response.getAmount())
          .rawJson(response.getRawJson())
          .build();
    } else if (in instanceof GeminiTransactionTransferResponse) {
      GeminiTransactionTransferResponse response = (GeminiTransactionTransferResponse) in;
      return FundingRecord.builder()
//          .status(status)
//          .type(type)
          .fromWallet(response.getSource())
          .toWallet(response.getDestination())
          .currency(new Currency(response.getCurrency()))
          .amount(response.getAmount())
          .blockchainTransactionHash(response.getTransactionHash())
          .internalId(response.getTransferId())
          .date(new Date(response.getTimestamp()))
          .description(response.getClientTransferId())
          .rawJson(response.getRawJson())
          .build();
    }
    return null;
  }

  @Getter
  public static class OrdersContainer {

    private final long timestamp;
    private final List<LimitOrder> limitOrders;

    /**
     * Constructor
     *
     * @param timestamp
     * @param limitOrders
     */
    public OrdersContainer(long timestamp, List<LimitOrder> limitOrders) {

      this.timestamp = timestamp;
      this.limitOrders = limitOrders;
    }

    public long getTimestamp() {

      return timestamp;
    }

    public List<LimitOrder> getLimitOrders() {

      return limitOrders;
    }
  }
}
