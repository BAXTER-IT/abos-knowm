package org.knowm.xchange.finerymarkets.service;

import static org.knowm.xchange.finerymarkets.FineryMarketsAdapters.adaptDealHistoryRequest;
import static org.knowm.xchange.finerymarkets.FineryMarketsAdapters.adaptUserTrades;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.finerymarkets.FineryMarketsExchange;
import org.knowm.xchange.finerymarkets.dto.trade.DealHistory;
import org.knowm.xchange.finerymarkets.dto.trade.request.DealHistoryRequest;
import org.knowm.xchange.finerymarkets.dto.trade.response.DealHistoryResponse;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;

public class FineryMarketsTradeService extends FineryMarketsTradeServiceRaw
    implements TradeService {

  public FineryMarketsTradeService(FineryMarketsExchange exchange) {
    super(exchange);
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {
    if (!(params instanceof TradeHistoryParamsAll)) {
      throw new IllegalArgumentException(
          "You need to provide a TradeHistoryParamsAll object in order to get all trades.");
    }

    DealHistoryRequest request = adaptDealHistoryRequest((TradeHistoryParamsAll) params);

    List<DealHistory> result = new ArrayList<>();

    DealHistoryResponse chunk;
    do {
      chunk = getDealHistory(request);
      result.addAll(chunk.getDeals());
      request.setTill(chunk.getTillId());
    } while (chunk.isFullPage());

    return adaptUserTrades(result);
  }
}
