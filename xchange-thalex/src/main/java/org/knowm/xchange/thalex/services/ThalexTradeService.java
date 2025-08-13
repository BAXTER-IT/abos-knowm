package org.knowm.xchange.thalex.services;

import static org.knowm.xchange.thalex.dto.enums.ThalexTradeType.NORMAL;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.knowm.xchange.dto.marketdata.Trades.TradeSortType;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsTimeSpan;
import org.knowm.xchange.thalex.ThalexAdapters;
import org.knowm.xchange.thalex.ThalexExchange;
import org.knowm.xchange.thalex.dto.trade.ThalexTradeDto;
import org.knowm.xchange.thalex.dto.trade.ThalexTradesDto;
import org.knowm.xchange.thalex.utils.FetchUtil;

public class ThalexTradeService extends ThalexTradeServiceRaw implements TradeService {

  public ThalexTradeService(ThalexExchange exchange) {
    super(exchange);
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {
    Long from;
    Long to;
    if (params instanceof TradeHistoryParamsTimeSpan) {
      TradeHistoryParamsTimeSpan paramsTimeSpan = ((TradeHistoryParamsTimeSpan) params);
      from = paramsTimeSpan.getStartTime() != null ? paramsTimeSpan.getStartTime().getTime() : null;
      to = paramsTimeSpan.getEndTime() != null ? paramsTimeSpan.getEndTime().getTime() : null;
    } else {
      to = null;
      from = null;
    }
    List<ThalexTradeDto> trades = FetchUtil.fetchAllPaginated(
        bookmark -> getThalexTrades(from, to, bookmark),
        ThalexTradesDto::getTrades,
        ThalexTradesDto::getBookmark
    );

    List<UserTrade> userTradeList =
        trades.stream()
            .filter(thalexTradeDto -> NORMAL.equals(thalexTradeDto.getTradeType()))
            .map(ThalexAdapters::toUserTrade).collect(Collectors.toList());
    return new UserTrades(userTradeList, TradeSortType.SortByID);
  }
}
