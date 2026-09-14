package org.knowm.xchange.coincall.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.coincall.CoincallExchange;
import org.knowm.xchange.coincall.dtos.CoincallPagedResponse;
import org.knowm.xchange.coincall.dtos.account.CoincallAccountSummaryDto;
import org.knowm.xchange.coincall.dtos.account.CoincallFuturesPositionDto;
import org.knowm.xchange.coincall.dtos.account.CoincallSubaccountTransferRecordDto;
import org.knowm.xchange.coincall.dtos.account.CoincallSystemTransferDto;
import org.knowm.xchange.coincall.dtos.account.CoincallTransactionDto;
import org.knowm.xchange.coincall.dtos.enums.CoincallTransactionHistoryQueryType;

public class CoincallAccountServiceRaw extends CoincallBaseService {

  public CoincallAccountServiceRaw(CoincallExchange exchange) {
    super(exchange);
  }

  CoincallAccountSummaryDto getCoincallAccountSummary() throws IOException {
    return coincallAuthenticated.accountSummary(coincallDigest).getData();
  }

  List<CoincallFuturesPositionDto> getCoincallFuturesPositions() throws IOException {
    return coincallAuthenticated.futuresPositions(coincallDigest).getData();
  }

  List<CoincallTransactionDto> getCoincallTransactionHistory(
      CoincallTransactionHistoryQueryType transactionType,
      Long startTime,
      Long endTime
  ) throws IOException {

    int page = 1;
    int pageSize = 1000;

    List<CoincallTransactionDto> all = new ArrayList<>();
    CoincallPagedResponse<CoincallTransactionDto> pageResponse;

    do {
      pageResponse = coincallAuthenticated.transactionHistory(
          coincallDigest,
          transactionType.getCode(),
          startTime,
          endTime,
          page,
          pageSize
      ).getData();

      if (pageResponse != null && pageResponse.getList() != null) {
        all.addAll(pageResponse.getList());
      }

      page++;
    } while (pageResponse != null && page <= pageResponse.getPageTotal());

    return all;
  }

  List<CoincallSubaccountTransferRecordDto> getCoincallSubaccountTransferRecords()
      throws IOException {

    int page = 1;
    int pageSize = 1000;

    List<CoincallSubaccountTransferRecordDto> all = new ArrayList<>();
    CoincallPagedResponse<CoincallSubaccountTransferRecordDto> pageResponse;

    do {
      pageResponse = coincallAuthenticated.subaccountTransferRecords(
          coincallDigest,
          page,
          pageSize
      ).getData();

      if (pageResponse != null && pageResponse.getList() != null) {
        all.addAll(pageResponse.getList());
      }

      page++;
    } while (pageResponse != null && page <= pageResponse.getPageTotal());

    return all;
  }

  List<CoincallSystemTransferDto> getCoincallSystemTransfers() throws IOException {
    return coincallAuthenticated.systemTransferRecords(coincallDigest).getData().getList();
  }
}
