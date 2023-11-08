package org.knowm.xchange.bybit.service;

import static org.knowm.xchange.bybit.BybitAdapters.adaptBybitOrderDetails;
import static org.knowm.xchange.bybit.BybitAdapters.convertToBybitSymbol;
import static org.knowm.xchange.bybit.BybitAdapters.getSideString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.bybit.BybitAdapters;
import org.knowm.xchange.bybit.dto.BybitCategory;
import org.knowm.xchange.bybit.dto.BybitResult;
import org.knowm.xchange.bybit.dto.trade.BybitOrderResponse;
import org.knowm.xchange.bybit.dto.trade.BybitOrderType;
import org.knowm.xchange.bybit.dto.trade.BybitTradeHistoryResponse;
import org.knowm.xchange.bybit.dto.trade.BybitUserTradeDto;
import org.knowm.xchange.bybit.dto.trade.details.BybitOrderDetail;
import org.knowm.xchange.bybit.dto.trade.details.BybitOrderDetails;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.Trades.TradeSortType;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.TradeHistoryParamId;
import org.knowm.xchange.service.trade.params.TradeHistoryParamInstrument;
import org.knowm.xchange.service.trade.params.TradeHistoryParamLimit;
import org.knowm.xchange.service.trade.params.TradeHistoryParamUserReference;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsTimeSpan;

public class BybitTradeService extends BybitTradeServiceRaw implements TradeService {

  private static final int MAX_PAGINATION_LIMIT = 100;
  private static final long SEVEN_DAYS_IN_MILLIS = 7 * 24 * 60 * 60 * 1000;

  public BybitTradeService(Exchange exchange) {
    super(exchange);
  }

  @Override
  public String placeMarketOrder(MarketOrder marketOrder) throws IOException {
    BybitResult<BybitOrderResponse> order =
        placeOrder(
            BybitCategory.SPOT,
            convertToBybitSymbol(marketOrder.getInstrument().toString()),
            getSideString(marketOrder.getType()),
            BybitOrderType.MARKET,
            marketOrder.getOriginalAmount());

    return order.getResult().getOrderId();
  }

  @Override
  public Collection<Order> getOrder(String... orderIds) throws IOException {
    List<Order> results = new ArrayList<>();

    for (String orderId : orderIds) {
      BybitResult<BybitOrderDetails<BybitOrderDetail>> bybitOrder =
          getBybitOrder(BybitCategory.SPOT, orderId);
      BybitOrderDetail bybitOrderResult = bybitOrder.getResult().getList().get(0);
      Order order = adaptBybitOrderDetails(bybitOrderResult);
      results.add(order);
    }

    return results;
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {

    if (!(params instanceof TradeHistoryParamInstrument)) {
      throw new IOException(
          "Params must be instance of " + TradeHistoryParamInstrument.class.getSimpleName());
    }

    Instrument instrument = ((TradeHistoryParamInstrument) params).getInstrument();
    BybitCategory category =
        BybitAdapters.getBybitCategoryFromInstrument(instrument, BybitCategory.SPOT);
    String orderId = null;
    String userReference = null;
    Date startTime = null;
    Date endTime = null;
    Integer limit = null;

    if (params instanceof TradeHistoryParamId) {
      orderId = ((TradeHistoryParamId) params).getId();
    }

    if (params instanceof TradeHistoryParamUserReference) {
      userReference = ((TradeHistoryParamUserReference) params).getUserReference();
    }

    if (params instanceof TradeHistoryParamsTimeSpan) {
      startTime = ((TradeHistoryParamsTimeSpan) params).getStartTime();
      endTime = ((TradeHistoryParamsTimeSpan) params).getEndTime();
    }

    if (params instanceof TradeHistoryParamLimit) {
      limit = ((TradeHistoryParamLimit) params).getLimit();
    }

    Long fromMillis = startTime == null ? null : startTime.getTime();
    Long toMillis = endTime == null ? null : endTime.getTime();
    limit = limit == null ? MAX_PAGINATION_LIMIT : Math.min(limit, MAX_PAGINATION_LIMIT);

    List<UserTrade> userTrades = new ArrayList<>();

    if (fromMillis != null && toMillis != null) {
      if (fromMillis > toMillis) {
        Long temp = fromMillis;
        fromMillis = toMillis;
        toMillis = temp;
      }
      double sevenDaySpans = Math.ceil((double) (toMillis - fromMillis) / SEVEN_DAYS_IN_MILLIS);
      for (int i = 0; i < sevenDaySpans; i++) {
        long newFrom = fromMillis + ((long) i * SEVEN_DAYS_IN_MILLIS);
        long newTo = Math.min(toMillis, newFrom + SEVEN_DAYS_IN_MILLIS);
        userTrades.addAll(
            getPaginatedTradeHistory(
                category, instrument, orderId, userReference, newFrom, newTo, limit));
      }
    } else {
      userTrades.addAll(
          getPaginatedTradeHistory(
              category, instrument, orderId, userReference, fromMillis, toMillis, limit));
    }

    return new UserTrades(userTrades, TradeSortType.SortByTimestamp);
  }

  private List<UserTrade> getPaginatedTradeHistory(
      BybitCategory category,
      Instrument instrument,
      String orderId,
      String userReference,
      Long startTime,
      Long endTime,
      int limit)
      throws IOException {

    String nextPageCursor = null;

    List<UserTrade> userTradeList = new ArrayList<>();

    do {
      BybitTradeHistoryResponse res =
          getBybitTradeHistory(
                  category,
                  instrument,
                  orderId,
                  userReference,
                  null,
                  startTime,
                  endTime,
                  null,
                  limit,
                  nextPageCursor)
              .getResult();

      userTradeList.addAll(BybitAdapters.adaptUserTrades(res));

      if (res.getTradeHistoryList().size() < limit) {
        break;
      }

      nextPageCursor = res.getNextPageCursor();

    } while (nextPageCursor != null && !nextPageCursor.isEmpty());

    return userTradeList;
  }
}
