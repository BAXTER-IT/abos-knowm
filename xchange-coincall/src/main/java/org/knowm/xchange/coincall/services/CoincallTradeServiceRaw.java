package org.knowm.xchange.coincall.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.knowm.xchange.coincall.CoincallExchange;
import org.knowm.xchange.coincall.configs.ThrowingBiFunction;
import org.knowm.xchange.coincall.dtos.CoincallPaginatedResponse;
import org.knowm.xchange.coincall.dtos.trade.CoincallFuturesTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallOptionTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallSpotFillDto;

public class CoincallTradeServiceRaw extends CoincallBaseService {

  public CoincallTradeServiceRaw(CoincallExchange exchange) {
    super(exchange);
  }

  List<CoincallSpotFillDto> getCoincallSpotFills(String symbol, Long orderId, Long startTime,
      Long endTime) throws IOException {
    return coincallAuthenticated
        .spotFills(coincallDigest, symbol, orderId, 1000, startTime, endTime)
        .getData();
  }

  CoincallPaginatedResponse<CoincallFuturesTransactionDetail> getFuturesTransactionDetailsPage(
      Integer pageSize,
      Long fromId,
      Long startTime,
      Long endTime
  ) throws IOException {
    return coincallAuthenticated
        .futuresTransactionDetails(coincallDigest, pageSize, fromId, startTime, endTime)
        .getData();
  }

  public List<CoincallFuturesTransactionDetail> getAllFuturesTransactionDetails(
      Long startTime,
      Long endTime
  ) throws IOException {

    return paginate(
        (pageSize, fromId) ->
            coincallAuthenticated
                .futuresTransactionDetails(
                    coincallDigest,
                    pageSize,
                    fromId,
                    startTime,
                    endTime
                ).getData(),
        CoincallFuturesTransactionDetail::getTradeId,
        500
    );
  }

  CoincallPaginatedResponse<CoincallOptionTransactionDetail> getOptionTransactionDetailsPage(
      Integer pageSize,
      Long fromId,
      Long startTime,
      Long endTime
  ) throws IOException {

    return coincallAuthenticated
        .optionTransactionDetails(coincallDigest, pageSize, fromId, startTime, endTime)
        .getData();
  }

  public List<CoincallOptionTransactionDetail> getAllOptionTransactionDetails(
      Long startTime,
      Long endTime
  ) throws IOException {

    return paginate(
        (pageSize, fromId) ->
            coincallAuthenticated
                .optionTransactionDetails(
                    coincallDigest,
                    pageSize,
                    fromId,
                    startTime,
                    endTime
                ).getData(),
        CoincallOptionTransactionDetail::getTradeId,
        500
    );
  }

  <T> List<T> paginate(
      ThrowingBiFunction<Integer, Long, CoincallPaginatedResponse<T>> pageFetcher,
      Function<T, Long> idExtractor,
      Integer pageSize
  ) throws IOException {

    List<T> result = new ArrayList<>();
    Long fromTradeId = null;

    while (true) {
      CoincallPaginatedResponse<T> page = pageFetcher.apply(pageSize, fromTradeId);

      if (page == null || page.getList() == null || page.getList().isEmpty()) {
        break;
      }

      List<T> list = page.getList();
      result.addAll(list);

      if (!page.isHasNext()) {
        break;
      }

      // next page
      fromTradeId = idExtractor.apply(list.get(list.size() - 1));
    }

    return result;
  }
}
