package org.knowm.xchange.bitget;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.knowm.xchange.bitget.dto.account.BitgetAccountType;
import org.knowm.xchange.bitget.dto.account.BitgetBalanceDto;
import org.knowm.xchange.bitget.dto.account.BitgetDepositWithdrawRecordDto;
import org.knowm.xchange.bitget.dto.account.BitgetDepositWithdrawRecordDto.DepositType;
import org.knowm.xchange.bitget.dto.account.BitgetDepositWithdrawRecordDto.RecordType;
import org.knowm.xchange.bitget.dto.account.BitgetMainSubTransferRecordDto;
import org.knowm.xchange.bitget.dto.account.BitgetTransferRecordDto;
import org.knowm.xchange.bitget.dto.account.params.BitgetMainSubTransferHistoryParams;
import org.knowm.xchange.bitget.dto.account.params.BitgetMainSubTransferHistoryParams.Role;
import org.knowm.xchange.bitget.dto.account.params.BitgetTransferHistoryParams;
import org.knowm.xchange.bitget.dto.marketdata.BitgetMarketDepthDto;
import org.knowm.xchange.bitget.dto.marketdata.BitgetSymbolDto;
import org.knowm.xchange.bitget.dto.marketdata.BitgetSymbolDto.Status;
import org.knowm.xchange.bitget.dto.marketdata.BitgetTickerDto;
import org.knowm.xchange.bitget.dto.trade.BitgetFillDto;
import org.knowm.xchange.bitget.dto.trade.BitgetOrderInfoDto;
import org.knowm.xchange.bitget.dto.trade.BitgetOrderInfoDto.BitgetOrderStatus;
import org.knowm.xchange.bitget.dto.trade.BitgetPlaceOrderDto;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderStatus;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Type;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.params.FundingRecordParamAll;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.enums.MarketParticipant;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;

@UtilityClass
public class BitgetAdapters {

  private final Map<String, CurrencyPair> SYMBOL_TO_CURRENCY_PAIR = new HashMap<>();

  public CurrencyPair toCurrencyPair(String symbol) {
    return SYMBOL_TO_CURRENCY_PAIR.get(symbol);
  }

  public String toString(Instrument instrument) {
    return instrument == null
        ? null
        : instrument.getBase().toString() + instrument.getCounter().toString();
  }

  public String toString(Currency currency) {
    return Optional.ofNullable(currency).map(Currency::getCurrencyCode).orElse(null);
  }

  public void putSymbolMapping(String symbol, CurrencyPair currencyPair) {
    SYMBOL_TO_CURRENCY_PAIR.put(symbol, currencyPair);
  }

  public InstrumentMetaData toInstrumentMetaData(BitgetSymbolDto bitgetSymbolDto) {
    InstrumentMetaData.Builder builder =
        new InstrumentMetaData.Builder()
            .tradingFee(bitgetSymbolDto.getTakerFeeRate())
            .minimumAmount(bitgetSymbolDto.getMinTradeAmount())
            .maximumAmount(bitgetSymbolDto.getMaxTradeAmount())
            .volumeScale(bitgetSymbolDto.getQuantityPrecision())
            .priceScale(bitgetSymbolDto.getPricePrecision())
            .marketOrderEnabled(bitgetSymbolDto.getStatus() == Status.ONLINE);

    // set min quote amount for USDT
    if (bitgetSymbolDto.getCurrencyPair().getCounter().equals(Currency.USDT)) {
      builder.counterMinimumAmount(bitgetSymbolDto.getMinTradeUSDT());
    }

    return builder.build();
  }

  public Ticker toTicker(BitgetTickerDto bitgetTickerDto) {
    CurrencyPair currencyPair = toCurrencyPair(bitgetTickerDto.getSymbol());
    if (currencyPair == null) {
      return null;
    }
    return new Ticker.Builder()
        .instrument(currencyPair)
        .open(bitgetTickerDto.getOpen24h())
        .last(bitgetTickerDto.getLastPrice())
        .bid(bitgetTickerDto.getBestBidPrice())
        .ask(bitgetTickerDto.getBestAskPrice())
        .high(bitgetTickerDto.getHigh24h())
        .low(bitgetTickerDto.getLow24h())
        .volume(bitgetTickerDto.getAssetVolume24h())
        .quoteVolume(bitgetTickerDto.getQuoteVolume24h())
        .timestamp(toDate(bitgetTickerDto.getTimestamp()))
        .bidSize(bitgetTickerDto.getBestBidSize())
        .askSize(bitgetTickerDto.getBestAskSize())
        .percentageChange(bitgetTickerDto.getChange24h())
        .build();
  }

  public Date toDate(Instant instant) {
    return Optional.ofNullable(instant).map(Date::from).orElse(null);
  }

  public Balance toBalance(BitgetBalanceDto balance) {
    return new Balance.Builder()
        .currency(balance.getCurrency())
        .available(balance.getAvailable())
        .frozen(balance.getFrozen())
        .timestamp(toDate(balance.getTimestamp()))
        .build();
  }

  public Wallet toWallet(List<BitgetBalanceDto> bitgetBalanceDtos) {
    List<Balance> balances =
        bitgetBalanceDtos.stream().map(BitgetAdapters::toBalance).collect(Collectors.toList());

    return Wallet.Builder.from(balances).id("spot").build();
  }

  public OrderBook toOrderBook(BitgetMarketDepthDto bitgetMarketDepthDto, Instrument instrument) {
    List<LimitOrder> asks =
        bitgetMarketDepthDto.getAsks().stream()
            .map(
                priceSizeEntry ->
                    new LimitOrder(
                        OrderType.ASK,
                        priceSizeEntry.getSize(),
                        instrument,
                        null,
                        null,
                        priceSizeEntry.getPrice()))
            .collect(Collectors.toList());

    List<LimitOrder> bids =
        bitgetMarketDepthDto.getBids().stream()
            .map(
                priceSizeEntry ->
                    new LimitOrder(
                        OrderType.BID,
                        priceSizeEntry.getSize(),
                        instrument,
                        null,
                        null,
                        priceSizeEntry.getPrice()))
            .collect(Collectors.toList());

    return new OrderBook(toDate(bitgetMarketDepthDto.getTimestamp()), asks, bids);
  }

  public Order toOrder(BitgetOrderInfoDto order) {
    if (order == null) {
      return null;
    }

    Instrument instrument = toCurrencyPair(order.getSymbol());
    Objects.requireNonNull(instrument);
    OrderType orderType = order.getOrderSide();

    Order.Builder builder;
    switch (order.getOrderType()) {
      case MARKET:
        builder = new MarketOrder.Builder(orderType, instrument);
        break;
      case LIMIT:
        builder = new LimitOrder.Builder(orderType, instrument).limitPrice(order.getPrice());
        break;
      default:
        throw new IllegalArgumentException("Can't map " + order.getOrderType());
    }

    if (orderType == OrderType.BID) {
      // buy orders fill quote
      builder.cumulativeAmount(order.getQuoteVolume());
    } else if (orderType == OrderType.ASK) {
      // sell orders fill asset
      builder.cumulativeAmount(order.getBaseVolume());
    } else {
      throw new IllegalArgumentException("Can't map " + orderType);
    }

    BigDecimal fee = order.getFee();
    if (fee != null) {
      builder.fee(fee);
    }

    return builder
        .id(String.valueOf(order.getOrderId()))
        .averagePrice(order.getPriceAvg())
        .originalAmount(order.getSize())
        .userReference(order.getClientOid())
        .timestamp(toDate(order.getCreatedAt()))
        .orderStatus(toOrderStatus(order.getOrderStatus()))
        .build();
  }

  public OrderStatus toOrderStatus(BitgetOrderStatus bitgetOrderStatus) {
    switch (bitgetOrderStatus) {
      case PENDING:
        return OrderStatus.NEW;
      case PARTIALLY_FILLED:
        return OrderStatus.PARTIALLY_FILLED;
      case FILLED:
        return OrderStatus.FILLED;
      case CANCELLED:
        return OrderStatus.CANCELED;
      default:
        throw new IllegalArgumentException("Can't map " + bitgetOrderStatus);
    }
  }

  public BitgetPlaceOrderDto toBitgetPlaceOrderDto(MarketOrder marketOrder) {
    return BitgetPlaceOrderDto.builder()
        .symbol(toString(marketOrder.getInstrument()))
        .orderSide(marketOrder.getType())
        .orderType(BitgetOrderInfoDto.OrderType.MARKET)
        .clientOid(marketOrder.getUserReference())
        .size(marketOrder.getOriginalAmount())
        .build();
  }

  public UserTrade toUserTrade(BitgetFillDto bitgetFillDto) {
    MarketParticipant marketParticipant;
    switch (bitgetFillDto.getTradeScope()) {
      case TAKER:
        marketParticipant = MarketParticipant.TAKER;
        break;
      case MAKER:
        marketParticipant = MarketParticipant.MAKER;
        break;
      default:
        throw new IllegalArgumentException("Can't map " + bitgetFillDto.getTradeScope());
    }
    return new UserTrade(
        bitgetFillDto.getOrderSide(),
        bitgetFillDto.getAssetAmount(),
        toCurrencyPair(bitgetFillDto.getSymbol()),
        bitgetFillDto.getPrice(),
        toDate(bitgetFillDto.getUpdatedAt()),
        bitgetFillDto.getTradeId(),
        bitgetFillDto.getOrderId(),
        bitgetFillDto.getFeeDetail().getTotalFee().abs(),
        bitgetFillDto.getFeeDetail().getCurrency(),
        null,
        marketParticipant,
        bitgetFillDto.getRawJson());
  }

  public String toString(BitgetAccountType bitgetAccountType) {
    return Optional.ofNullable(bitgetAccountType).map(BitgetAccountType::getValue).orElse(null);
  }

  public String toString(BitgetMainSubTransferHistoryParams.Role role) {
    return Optional.ofNullable(role).map(Role::getValue).orElse(null);
  }

  public FundingRecord toFundingRecord(BitgetDepositWithdrawRecordDto in) {
    return new FundingRecord.Builder()
        .setInternalId(in.getOrderId())
        .setBlockchainTransactionHash(in.getTradeId())
        .setCurrency(in.getCurrency())
        .setType(toFundingRecordType(in))
        .setAmount(in.getSize())
        .setFee(in.getFee())
        .setStatus(in.getStatus())
        .setAddress(in.getToAddress())
        .setAddressTag(in.getToAddressTag())
        .setDate(toDate(in.getUpdatedAt()))
        .build();
  }

  public FundingRecord toFundingRecord(BitgetMainSubTransferRecordDto in) {
    return FundingRecord.builder()
        .internalId(in.getTransferId())
        .currency(in.getCurrency())
        .amount(in.getSize())
        .fee(BigDecimal.ZERO)
        .status(in.getStatus())
        .address(in.getToUserId())
        .date(toDate(in.getTimestamp()))
        .fromWallet(in.getFromAccountType().getValue())
        .toWallet(in.getToAccountType().getValue())
        .fromSubAccount(in.getFromUserId())
        .toSubAccount(in.getToUserId())
        .build();
  }

  public static FundingRecord toFundingRecord(BitgetTransferRecordDto in) {
    return FundingRecord.builder()
        .internalId(in.getTransferId())
        .currency(in.getCurrency())
        .amount(in.getSize())
        .fee(BigDecimal.ZERO)
        .status(in.getStatus())
        .date(toDate(in.getTimestamp()))
        .rawJson(in.getRawJson())
        .build();
  }

  public FundingRecord.Type toFundingRecordType(BitgetDepositWithdrawRecordDto in) {
    if (in.getDepositType() == DepositType.ON_CHAIN && in.getType() == RecordType.WITHDRAW) {
      return Type.WITHDRAWAL;
    }
    if (in.getDepositType() == DepositType.ON_CHAIN && in.getType() == RecordType.DEPOSIT) {
      return Type.DEPOSIT;
    }
    if (in.getDepositType() == DepositType.INTERNAL_TRANSFER
        && in.getType() == RecordType.WITHDRAW) {
      return Type.INTERNAL_WITHDRAWAL;
    }
    if (in.getDepositType() == DepositType.INTERNAL_TRANSFER
        && in.getType() == RecordType.DEPOSIT) {
      return Type.INTERNAL_DEPOSIT;
    }

    return null;
  }

  public static TradeHistoryParamsAll toTradeHistoryParamsAll(FundingRecordParamAll params) {
    TradeHistoryParamsAll tradeHistoryParamsAll = new TradeHistoryParamsAll();
    tradeHistoryParamsAll.setCurrency(params.getCurrency());
    tradeHistoryParamsAll.setOrderId(params.getOrderId());
    tradeHistoryParamsAll.setLimit(params.getLimit());
    tradeHistoryParamsAll.setEndId(params.getEndId());
    tradeHistoryParamsAll.setStartTime(params.getStartTime());
    tradeHistoryParamsAll.setEndTime(params.getEndTime());
    tradeHistoryParamsAll.setSubaccountId(params.getSubAccountId());
    return tradeHistoryParamsAll;
  }

  public static BitgetMainSubTransferHistoryParams toMainSubTransferHistoryParams(
      FundingRecordParamAll params) {
    Instant to = Instant.now();
    Instant from = to.minus(89, java.time.temporal.ChronoUnit.DAYS);
    if (params.getStartTime() != null) {
      from = params.getStartTime().toInstant();
    }
    if (params.getEndTime() != null) {
      to = params.getEndTime().toInstant();
    }
    return BitgetMainSubTransferHistoryParams.builder()
        .currency(params.getCurrency())
        .subAccountUid(params.getSubAccountId())
        .clientOid(params.getTransferId())
        .limit(params.getLimit())
        .startTime(from)
        .endTime(to)
        .build();
  }

  public static BitgetTransferHistoryParams toTransferHistoryParams(FundingRecordParamAll params) {
    Instant to = Instant.now();
    Instant from = to.minus(89, java.time.temporal.ChronoUnit.DAYS);
    if (params.getStartTime() != null) {
      from = params.getStartTime().toInstant();
    }
    if (params.getEndTime() != null) {
      to = params.getEndTime().toInstant();
    }
    return BitgetTransferHistoryParams.builder()
        .currency(params.getCurrency())
        .clientOid(params.getTransferId())
        .fromAccountType(BitgetAccountType.SPOT)
        .limit(params.getLimit())
        .startTime(from)
        .endTime(to)
        .build();
  }
}
