package info.bitrich.xchangestream.finerymarkets;

import info.bitrich.xchangestream.core.StreamingTradeService;
import io.reactivex.Observable;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.finerymarkets.FineryMarketsAdapters;

public class FineryMarketsStreamingTradeService implements StreamingTradeService {

  FineryMarketsStreamingExchange streamingExchange;

  public FineryMarketsStreamingTradeService(FineryMarketsStreamingExchange fineryMarketsStreamingExchange) {
    this.streamingExchange = fineryMarketsStreamingExchange;
  }

  @Override
  public Observable<UserTrade> getUserTrades() {
    return streamingExchange
        .getApplication()
        .getExecutionReportSubject()
        .map(FineryMarketsAdapters::adaptExecutionReport);
  }
}
