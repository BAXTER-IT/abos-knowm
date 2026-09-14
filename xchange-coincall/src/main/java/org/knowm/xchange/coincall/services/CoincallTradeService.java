package org.knowm.xchange.coincall.services;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.coincall.CoincallAdapters;
import org.knowm.xchange.coincall.CoincallExchange;
import org.knowm.xchange.coincall.dtos.trade.CoincallFuturesTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallOptionTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallSpotFillDto;
import org.knowm.xchange.dto.marketdata.Trades.TradeSortType;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.TradeHistoryParamInstrument;
import org.knowm.xchange.service.trade.params.TradeHistoryParamOrderId;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsTimeSpan;

@Slf4j
public class CoincallTradeService extends CoincallTradeServiceRaw implements TradeService {

  public CoincallTradeService(CoincallExchange exchange) {
    super(exchange);
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {
    List<UserTrade> userTrades = getSpotUserTrades(params);
    userTrades.addAll(getFuturesUserTrades(params));
    userTrades.addAll(getOptionsUserTrades(params));
    return new UserTrades(userTrades, TradeSortType.SortByTimestamp);
  }

  List<UserTrade> getSpotUserTrades(TradeHistoryParams params) throws IOException {
    String symbol = null;
    Long orderId = null;
    Long startTime = null;
    Long endTime = null;
    if (params instanceof TradeHistoryParamInstrument) {
      if (((TradeHistoryParamInstrument) params).getInstrument() != null) {
        symbol = ((TradeHistoryParamInstrument) params).getInstrument().toString().replace("/", "");
      }
    }
    if (params instanceof TradeHistoryParamOrderId) {
      if (((TradeHistoryParamOrderId) params).getOrderId() != null) {
        orderId = Long.valueOf(((TradeHistoryParamOrderId) params).getOrderId());
      }
    }
    if (params instanceof TradeHistoryParamsTimeSpan) {
      TradeHistoryParamsTimeSpan paramsTimeSpan = ((TradeHistoryParamsTimeSpan) params);
      startTime =
          paramsTimeSpan.getStartTime() != null ? paramsTimeSpan.getStartTime().getTime() : null;
      endTime = paramsTimeSpan.getEndTime() != null ? paramsTimeSpan.getEndTime().getTime() : null;
    }

    List<CoincallSpotFillDto> coincallFills = getCoincallSpotFills(symbol, orderId, startTime,
        endTime);
    return coincallFills.stream().map(CoincallAdapters::toUserTrade)
        .collect(Collectors.toList());
  }

  List<UserTrade> getFuturesUserTrades(TradeHistoryParams params) throws IOException {
    Long startTime = null;
    Long endTime = null;

    if (params instanceof TradeHistoryParamsTimeSpan) {
      TradeHistoryParamsTimeSpan span = (TradeHistoryParamsTimeSpan) params;
      startTime = span.getStartTime() != null ? span.getStartTime().getTime() : null;
      endTime = span.getEndTime() != null ? span.getEndTime().getTime() : null;
    }

    List<CoincallFuturesTransactionDetail> details =
        getAllFuturesTransactionDetails(startTime, endTime);

    return details.stream()
        .map(CoincallAdapters::toUserTrade)
        .collect(Collectors.toList());
  }

  List<UserTrade> getOptionsUserTrades(TradeHistoryParams params) throws IOException {
    Long startTime = null;
    Long endTime = null;

    if (params instanceof TradeHistoryParamsTimeSpan) {
      TradeHistoryParamsTimeSpan span = (TradeHistoryParamsTimeSpan) params;
      startTime = span.getStartTime() != null ? span.getStartTime().getTime() : null;
      endTime = span.getEndTime() != null ? span.getEndTime().getTime() : null;
    }

    List<CoincallOptionTransactionDetail> details =
        getAllOptionTransactionDetails(startTime, endTime);

    return details.stream()
        .map(CoincallAdapters::toUserTrade)
        .collect(Collectors.toList());
  }
}
