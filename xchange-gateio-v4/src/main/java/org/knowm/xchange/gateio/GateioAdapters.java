package org.knowm.xchange.gateio;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderStatus;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Status;
import org.knowm.xchange.dto.account.FundingRecord.Type;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.gateio.dto.account.GateioAccountBookRecord;
import org.knowm.xchange.gateio.dto.account.GateioDepositRecord;
import org.knowm.xchange.gateio.dto.account.GateioOrder;
import org.knowm.xchange.gateio.dto.account.GateioSubAccountTransfer;
import org.knowm.xchange.gateio.dto.account.GateioWithdrawalRecord;
import org.knowm.xchange.gateio.dto.account.GateioWithdrawalRequest;
import org.knowm.xchange.gateio.dto.marketdata.GateioCurrencyPairDetails;
import org.knowm.xchange.gateio.dto.marketdata.GateioOrderBook;
import org.knowm.xchange.gateio.dto.marketdata.GateioTicker;
import org.knowm.xchange.gateio.dto.trade.GateioUserTrade;
import org.knowm.xchange.gateio.dto.trade.GateioUserTradeRaw;
import org.knowm.xchange.gateio.service.params.GateioWithdrawFundsParams;
import org.knowm.xchange.instrument.Instrument;

@UtilityClass
public class GateioAdapters {

  public String toString(Instrument instrument) {
    if (instrument == null) {
      return null;
    } else {
      return String.format(
              "%s_%s",
              instrument.getBase().getCurrencyCode(), instrument.getCounter().getCurrencyCode())
          .toUpperCase(Locale.ROOT);
    }
  }

  public OrderBook toOrderBook(GateioOrderBook gateioOrderBook, Instrument instrument) {
    List<LimitOrder> asks =
        gateioOrderBook.getAsks().stream()
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
        gateioOrderBook.getBids().stream()
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

    return new OrderBook(Date.from(gateioOrderBook.getGeneratedAt()), asks, bids);
  }

  public InstrumentMetaData toInstrumentMetaData(
      GateioCurrencyPairDetails gateioCurrencyPairDetails) {
    return new InstrumentMetaData.Builder()
        .tradingFee(gateioCurrencyPairDetails.getFee())
        .minimumAmount(gateioCurrencyPairDetails.getMinAssetAmount())
        .counterMinimumAmount(gateioCurrencyPairDetails.getMinQuoteAmount())
        .volumeScale(gateioCurrencyPairDetails.getAssetScale())
        .priceScale(gateioCurrencyPairDetails.getQuoteScale())
        .build();
  }

  public String toString(OrderStatus orderStatus) {
    switch (orderStatus) {
      case OPEN:
        return "open";
      case CLOSED:
        return "finished";
      default:
        throw new IllegalArgumentException("Can't map " + orderStatus);
    }
  }

  public OrderStatus toOrderStatus(String gateioOrderStatus) {
    switch (gateioOrderStatus) {
      case "open":
        return OrderStatus.OPEN;
      case "filled":
      case "closed":
        return OrderStatus.FILLED;
      case "cancelled":
      case "stp":
        return OrderStatus.CANCELED;
      default:
        throw new IllegalArgumentException("Can't map " + gateioOrderStatus);
    }
  }

  public GateioOrder toGateioOrder(MarketOrder marketOrder) {
    return GateioOrder.builder()
        .currencyPair((CurrencyPair) marketOrder.getInstrument())
        .side(marketOrder.getType())
        .clientOrderId(marketOrder.getUserReference())
        .account("spot")
        .type("market")
        .timeInForce("ioc")
        .amount(marketOrder.getOriginalAmount())
        .build();
  }

  public GateioOrder toGateioOrder(LimitOrder limitOrder) {
    return GateioOrder.builder()
        .currencyPair((CurrencyPair) limitOrder.getInstrument())
        .side(limitOrder.getType())
        .clientOrderId(limitOrder.getUserReference())
        .account("spot")
        .type("limit")
        .timeInForce("gtc")
        .price(limitOrder.getLimitPrice())
        .amount(limitOrder.getOriginalAmount())
        .build();
  }

  public Order toOrder(GateioOrder gateioOrder) {
    Order.Builder order;
    Instrument instrument = gateioOrder.getCurrencyPair();
    OrderType orderType = gateioOrder.getSide();

    switch (gateioOrder.getType()) {
      case "market":
        order = new MarketOrder.Builder(orderType, instrument);
        break;
      case "limit":
        order = new LimitOrder.Builder(orderType, instrument).limitPrice(gateioOrder.getPrice());
        break;
      default:
        throw new IllegalArgumentException("Can't map " + gateioOrder.getType());
    }

    return order
        .id(gateioOrder.getId())
        .originalAmount(gateioOrder.getAmount())
        .userReference(gateioOrder.getClientOrderId())
        .timestamp(Date.from(gateioOrder.getCreatedAt()))
        .orderStatus(toOrderStatus(gateioOrder.getStatus()))
        .cumulativeAmount(gateioOrder.getFilledTotalQuote())
        .averagePrice(gateioOrder.getAvgDealPrice())
        .fee(gateioOrder.getFee())
        .build();
  }

  public UserTrade toUserTrade(GateioUserTradeRaw gateioUserTradeRaw) {
    return new GateioUserTrade(
        gateioUserTradeRaw.getSide(),
        gateioUserTradeRaw.getAmount(),
        gateioUserTradeRaw.getCurrencyPair(),
        gateioUserTradeRaw.getPrice(),
        Date.from(gateioUserTradeRaw.getTimeMs()),
        String.valueOf(gateioUserTradeRaw.getId()),
        String.valueOf(gateioUserTradeRaw.getOrderId()),
        gateioUserTradeRaw.getFee(),
        gateioUserTradeRaw.getFeeCurrency(),
        gateioUserTradeRaw.getRemark(),
        gateioUserTradeRaw.getRole());
  }

  public GateioWithdrawalRequest toGateioWithdrawalRequest(GateioWithdrawFundsParams p) {
    return GateioWithdrawalRequest.builder()
        .clientRecordId(p.getClientRecordId())
        .address(p.getAddress())
        .tag(p.getAddressTag())
        .chain(p.getChain())
        .amount(p.getAmount())
        .currency(p.getCurrency())
        .build();
  }

  public Ticker toTicker(GateioTicker gateioTicker) {
    return new Ticker.Builder()
        .instrument(gateioTicker.getCurrencyPair())
        .last(gateioTicker.getLastPrice())
        .bid(gateioTicker.getHighestBid())
        .ask(gateioTicker.getLowestAsk())
        .high(gateioTicker.getMaxPrice24h())
        .low(gateioTicker.getMinPrice24h())
        .volume(gateioTicker.getAssetVolume())
        .quoteVolume(gateioTicker.getQuoteVolume())
        .percentageChange(gateioTicker.getChangePercentage24h())
        .build();
  }

  public FundingRecord toFundingRecords(GateioAccountBookRecord gateioAccountBookRecord) {
    return FundingRecord.builder()
        .internalId(gateioAccountBookRecord.getId())
        .date(Date.from(gateioAccountBookRecord.getTimestamp()))
        .currency(gateioAccountBookRecord.getCurrency())
        .balance(gateioAccountBookRecord.getBalance())
        .type(gateioAccountBookRecord.getType())
        .amount(gateioAccountBookRecord.getChange().abs())
        .description(gateioAccountBookRecord.getTypeDescription())
        .build();
  }

  public FundingRecord toFundingRecords(GateioSubAccountTransfer gateioSubAccountTransfer) {
    if ("from".equals(gateioSubAccountTransfer.getDirection())) {
      return FundingRecord.builder()
          .currency(gateioSubAccountTransfer.getCurrency())
          .date(Date.from(gateioSubAccountTransfer.getTimestamp()))
          .amount(gateioSubAccountTransfer.getAmount().abs())
          .type(Type.INTERNAL_SUB_ACCOUNT_TRANSFER)
          .fromSubAccount(gateioSubAccountTransfer.getSubAccountId().toString())
          .toWallet(gateioSubAccountTransfer.getMainAccountId().toString())
          .build();
    } else if("to".equals(gateioSubAccountTransfer.getDirection())){
      return FundingRecord.builder()
          .currency(gateioSubAccountTransfer.getCurrency())
          .date(Date.from(gateioSubAccountTransfer.getTimestamp()))
          .amount(gateioSubAccountTransfer.getAmount().abs())
          .type(Type.INTERNAL_SUB_ACCOUNT_TRANSFER)
          .fromWallet(gateioSubAccountTransfer.getMainAccountId().toString())
          .toSubAccount(gateioSubAccountTransfer.getSubAccountId().toString())
          .build();

    } else {
      throw new IllegalArgumentException("Can't map " + gateioSubAccountTransfer.getDirection());
    }
  }

  public static FundingRecord toFundingRecords(GateioWithdrawalRecord gateioWithdrawalRecord) {
    Status status;
    switch (gateioWithdrawalRecord.getStatus()) {
      case DONE:
        status = Status.COMPLETE;
        break;
      case CANCEL:
        status = Status.CANCELLED;
        break;
      case REQUEST:
      case MANUAL:
      case BCODE:
      case EXTPEND:
      case VERIFY:
      case PROCES:
      case PEND:
      case DMOVE:
      case SPLITPEND:
        status = Status.PROCESSING;
        break;
      case FAIL:
      case INVALID:
        status = Status.FAILED;
        break;
      default:
        throw new IllegalArgumentException("Can't map " + gateioWithdrawalRecord.getStatus());
    }
    return FundingRecord.builder()
        .internalId(gateioWithdrawalRecord.getId())
        .status(status)
        .date(Date.from(gateioWithdrawalRecord.getCreatedAt()))
        .currency(gateioWithdrawalRecord.getCurrency())
        .address(gateioWithdrawalRecord.getAddress())
        .amount(gateioWithdrawalRecord.getAmount())
        .fee(gateioWithdrawalRecord.getFee())
        .type(Type.WITHDRAWAL)
        .build();
  }

  public static FundingRecord toFundingRecords(GateioDepositRecord gateioDepositRecord) {
    Status status;
    switch (gateioDepositRecord.getStatus()) {
      case DONE:
        status = Status.COMPLETE;
        break;
      case CANCEL:
        status = Status.CANCELLED;
        break;
      case REQUEST:
      case MANUAL:
      case BCODE:
      case EXTPEND:
      case VERIFY:
      case PROCES:
      case PEND:
      case DMOVE:
      case SPLITPEND:
        status = Status.PROCESSING;
        break;
      case FAIL:
      case INVALID:
        status = Status.FAILED;
        break;
      default:
        throw new IllegalArgumentException("Can't map " + gateioDepositRecord.getStatus());
    }
    return FundingRecord.builder()
        .internalId(gateioDepositRecord.getId())
        .status(status)
        .date(Date.from(gateioDepositRecord.getCreatedAt()))
        .currency(gateioDepositRecord.getCurrency())
        .address(gateioDepositRecord.getAddress())
        .amount(gateioDepositRecord.getAmount())
        .type(Type.DEPOSIT)
        .build();
  }

  public Map<Instrument, InstrumentMetaData> toInstruments(
      List<GateioCurrencyPairDetails> currencyPairDetails) {
    return currencyPairDetails.stream()
        .filter(detail -> "tradable".equals(detail.getTradeStatus()))
        .collect(
            Collectors.toMap(
                detail -> new CurrencyPair(detail.getAsset(), detail.getQuote()),
                detail ->
                    new InstrumentMetaData.Builder()
                        .minimumAmount(detail.getMinAssetAmount())
                        .counterMinimumAmount(detail.getMinQuoteAmount())
                        .build()));
  }
}
