package org.knowm.xchange.coincall.services;

import static org.knowm.xchange.coincall.dtos.enums.CoincallTransactionHistoryQueryType.All;
import static org.knowm.xchange.coincall.dtos.enums.CoincallTransactionHistoryQueryType.DEPOSIT;
import static org.knowm.xchange.coincall.dtos.enums.CoincallTransactionHistoryQueryType.WITHDRAW;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.knowm.xchange.coincall.CoincallAdapters;
import org.knowm.xchange.coincall.CoincallExchange;
import org.knowm.xchange.coincall.dtos.account.CoincallAccountSummaryDto;
import org.knowm.xchange.coincall.dtos.account.CoincallFuturesPositionDto;
import org.knowm.xchange.coincall.dtos.account.CoincallSubaccountTransferRecordDto;
import org.knowm.xchange.coincall.dtos.account.CoincallSystemTransferDto;
import org.knowm.xchange.coincall.dtos.account.CoincallTransactionDto;
import org.knowm.xchange.coincall.dtos.enums.CoincallTransactionHistoryQueryType;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.params.FundingRecordParamAll;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;

public class CoincallAccountService extends CoincallAccountServiceRaw implements AccountService {

  public CoincallAccountService(CoincallExchange exchange) {
    super(exchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    CoincallAccountSummaryDto coincallAccountSummary = getCoincallAccountSummary();
    Wallet wallet = CoincallAdapters.toWallet(coincallAccountSummary.getAccounts());
    List<CoincallFuturesPositionDto> coincallFuturesPositionDtos = getCoincallFuturesPositions();
    List<OpenPosition> openPositions = CoincallAdapters.toOpenPositions(
        coincallFuturesPositionDtos);
    return new AccountInfo(Collections.singletonList(wallet), openPositions);
  }

  @Override
  public List<FundingRecord> getDepositHistory(FundingRecordParamAll params) throws IOException {
    return getCoincallTransactionDtos(params, DEPOSIT);
  }

  @Override
  public List<FundingRecord> getWithdrawHistory(FundingRecordParamAll params) throws IOException {
    return getCoincallTransactionDtos(params, WITHDRAW);
  }

  private List<FundingRecord> getCoincallTransactionDtos(FundingRecordParamAll params,
      CoincallTransactionHistoryQueryType queryType) throws IOException {
    Long startTime = params.getStartTime() != null ? params.getStartTime().getTime() : null;
    Long endTime = params.getEndTime() != null ? params.getEndTime().getTime() : null;

    List<CoincallTransactionDto> coincallTransactionHistory = getCoincallTransactionHistory(
        queryType, startTime,
        endTime);
    return CoincallAdapters.toFundingRecords(coincallTransactionHistory);
  }

  @Override
  public List<FundingRecord> getInternalTransferHistory(FundingRecordParamAll params)
      throws IOException {
    List<CoincallSubaccountTransferRecordDto> coincallSubaccountTransferRecords = getCoincallSubaccountTransferRecords();
    return CoincallAdapters.subaccountTransfersToFundingRecords(coincallSubaccountTransferRecords);
  }

  /**
   * Responses from transaction, transfers, system transfers and trades.
   */
  @Override
  public List<FundingRecord> getLedger(FundingRecordParamAll params) throws IOException {

    // Withdrawal, deposit
    List<FundingRecord> ledger = getCoincallTransactionDtos(params, All);

    // Transfers
    ledger.addAll(getInternalTransferHistory(params));

    // System transfers
    List<CoincallSystemTransferDto> coincallSystemTransfers = getCoincallSystemTransfers();
    ledger.addAll(coincallSystemTransfers.stream().map(CoincallAdapters::toFundingRecord)
        .collect(Collectors.toList()));

    // Trades
    CoincallTradeService coincallTradeService = new CoincallTradeService(exchange);
    TradeHistoryParamsAll tradeHistoryParams = new TradeHistoryParamsAll();
    UserTrades tradeHistory = coincallTradeService.getTradeHistory(tradeHistoryParams);
    ledger.addAll(tradeHistory.getUserTrades().stream()
        .map(CoincallAdapters::toFundingRecord).collect(Collectors.toList()));
    return ledger;
  }
}
