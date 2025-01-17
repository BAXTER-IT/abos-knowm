package org.knowm.xchange.finerymarkets;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.Trades.TradeSortType;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.enums.MarketParticipant;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsCurrency;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsInstrument;
import org.knowm.xchange.finerymarkets.dto.marketdata.response.InstrumentsResponse;
import org.knowm.xchange.finerymarkets.dto.trade.DealFilter;
import org.knowm.xchange.finerymarkets.dto.trade.DealHistory;
import org.knowm.xchange.finerymarkets.dto.trade.request.DealHistoryRequest;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;

@Slf4j
public class FineryMarketsAdapters {

  private FineryMarketsAdapters() {}

  public static Map<Currency, CurrencyMetaData> adaptCurrencies(InstrumentsResponse input) {
    if (input == null) {
      return Collections.emptyMap();
    }
    return input.getCurrencies().stream()
        .distinct()
        .collect(
            Collectors.toMap(
                FineryMarketsAdapters::adaptCurrency,
                FineryMarketsAdapters::adaptCurrencyMetaData));
  }

  public static Map<Instrument, InstrumentMetaData> adaptInstruments(InstrumentsResponse input) {
    if (input == null) {
      return Collections.emptyMap();
    }
    return input.getInstruments().stream()
        .distinct()
        .collect(
            Collectors.toMap(
                FineryMarketsAdapters::adaptInstrument,
                FineryMarketsAdapters::adaptInstrumentMetaData));
  }

  public static UserTrades adaptUserTrades(List<DealHistory> input) {
    if (input == null) {
      return new UserTrades(Collections.emptyList(), TradeSortType.SortByID);
    }
    return new UserTrades(
        input.stream().map(FineryMarketsAdapters::adaptUserTrade).collect(Collectors.toList()),
        TradeSortType.SortByID);
  }

  public static String adaptInstrument(CurrencyPair input) {
    if (input == null) {
      return null;
    }
    return input.toString().replace("/", "-");
  }

  public static DealHistoryRequest adaptDealHistoryRequest(TradeHistoryParamsAll input) {
    if (input == null) {
      return null;
    }
    DealFilter filterParam = (DealFilter) input.getCustomParam("filter");
    DealFilter filter = filterParam == null ? DealFilter.ALL : filterParam;

    return DealHistoryRequest.builder()
        .instrument(adaptInstrument(input.getCurrencyPair()))
        .filter(filter)
        .till(input.getEndId() == null ? null : Long.parseLong(input.getEndId()))
        .to(input.getEndTime() == null ? null : input.getEndTime().getTime())
        .from(input.getStartTime() == null ? null : input.getStartTime().getTime())
        .limit(input.getLimit() == null ? null : input.getLimit().shortValue())
        .build();
  }

  public static UserTrade adaptUserTrade(@NonNull DealHistory input) {
    OrderType orderType = null;
    // DealHistory (method input) also has an orderType field, but that means else
    switch (input.getSide()) {
      case BID:
        orderType = OrderType.BID;
        break;
      case ASK:
        orderType = OrderType.ASK;
        break;
      default:
        log.error("Unknown order type: {}", input.getSide());
    }

    MarketParticipant marketParticipant = null;
    switch (input.getOrderType()) {
      case LIMIT:
      case POST_ONLY:
      case LIMIT_IOC:
      case LIMIT_FOK:
        marketParticipant = MarketParticipant.MAKER;
        break;
      case MARKET_IOC:
      case MARKET_FOK:
      case MANUAL_TRADE:
      case LIQUIDATION_TRADE:
        marketParticipant = MarketParticipant.TAKER;
        break;
      default:
        log.error("Unknown market participant: {}", input.getOrderType());
    }

    CurrencyPair instrument = adaptCurrencyPair(input.getInstrumentName());

    return new UserTrade.Builder()
        .type(orderType)
        .originalAmount(BigDecimal.valueOf(input.getDealSize()).movePointLeft(8))
        .instrument(instrument)
        .price(BigDecimal.valueOf(input.getDealPrice()).movePointLeft(8))
        .timestamp(adaptDate(input.getDealMoment()))
        .id(String.valueOf(input.getDealId()))
        .orderId(String.valueOf(input.getOrderId()))
        .orderUserReference(String.valueOf(input.getCounterpartyId()))
        .marketParticipant(marketParticipant)
        .feeAmount(BigDecimal.ZERO)
        .feeCurrency(instrument.getBase())
        .rawJson(input.getRawJson())
        .build();
  }

  private static CurrencyPair adaptInstrument(FineryMarketsInstrument input) {
    if (input == null) {
      return null;
    }
    return new CurrencyPair(input.getAssetCurrencyName(), input.getBalanceCurrencyName());
  }

  private static InstrumentMetaData adaptInstrumentMetaData(FineryMarketsInstrument input) {
    if (input == null) {
      return null;
    }
    return new InstrumentMetaData.Builder().rawJson(input.getRawJson()).build();
  }

  private static Currency adaptCurrency(FineryMarketsCurrency input) {
    if (input == null) {
      return null;
    }
    return new Currency(input.getName());
  }

  private static CurrencyMetaData adaptCurrencyMetaData(FineryMarketsCurrency input) {
    if (input == null) {
      return null;
    }
    return CurrencyMetaData.builder()
        .scale(input.getBalanceStep())
        .rawJson(input.getRawJson())
        .build();
  }

  private static CurrencyPair adaptCurrencyPair(String input) {
    if (input == null) {
      return null;
    }
    return new CurrencyPair(input);
  }

  private static Date adaptDate(long input) {
    return new Date(input);
  }
}
