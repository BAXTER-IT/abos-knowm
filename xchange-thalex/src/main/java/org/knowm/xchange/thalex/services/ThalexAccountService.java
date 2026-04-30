package org.knowm.xchange.thalex.services;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.params.FundingRecordParamAll;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.thalex.ThalexAdapters;
import org.knowm.xchange.thalex.ThalexExchange;
import org.knowm.xchange.thalex.dto.account.ThalexAccountSummaryDto;
import org.knowm.xchange.thalex.dto.account.ThalexCryptoDeposits;
import org.knowm.xchange.thalex.dto.account.ThalexDailyMarkDto;
import org.knowm.xchange.thalex.dto.account.ThalexDailyMarkHistoryResult;
import org.knowm.xchange.thalex.dto.account.ThalexDeposit;
import org.knowm.xchange.thalex.dto.account.ThalexPortfolioDto;
import org.knowm.xchange.thalex.dto.account.ThalexTransactionDto;
import org.knowm.xchange.thalex.dto.account.ThalexTransactionsDto;
import org.knowm.xchange.thalex.dto.account.ThalexWithdrawal;
import org.knowm.xchange.thalex.exceptions.TransfersFetchException;
import org.knowm.xchange.thalex.utils.FetchUtil;

@Slf4j
public class ThalexAccountService extends ThalexAccountServiceRaw implements AccountService {

  public ThalexAccountService(ThalexExchange exchange) {
    super(exchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    // get account balances
    ThalexAccountSummaryDto balances = getThalexBalances();
    Wallet wallet = ThalexAdapters.toWallet(balances);
    // get account portfolios
    List<ThalexPortfolioDto> thalexPortfolios = getThalexPortfolio();
    List<OpenPosition> openPositions = ThalexAdapters.toOpenPositions(thalexPortfolios);
    return new AccountInfo(Collections.singletonList(wallet),
        openPositions);
  }

  @Override
  public List<FundingRecord> getWalletTransferHistory(FundingRecordParamAll params)
      throws TransfersFetchException {
    try {
      return getThalexTransactions(params);
    } catch (Exception e) {
      log.error("Failed to fetch transactions", e);
      throw new TransfersFetchException(e);
    }
  }

  @Override
  public List<FundingRecord> getDepositHistory(FundingRecordParamAll params) throws IOException {
    // input params are ignored
    ThalexCryptoDeposits deposits = getThalexCryptoDeposits();
    List<FundingRecord> fundingRecords = getFundingRecords(deposits.getConfirmed());
    fundingRecords.addAll(getFundingRecords(deposits.getUnconfirmed()));
    return fundingRecords;
  }

  @Override
  public List<FundingRecord> getWithdrawHistory(FundingRecordParamAll params) throws IOException {
    // input params are ignored
    List<ThalexWithdrawal> withdrawals = getThalexCryptoWithdrawals();
    return withdrawals.stream()
        .map(ThalexAdapters::toFundingRecord)
        .collect(Collectors.toList());
  }

  @Override
  public List<FundingRecord> getLedger(FundingRecordParamAll params)
      throws TransfersFetchException {
    try {
      return getThalexTransactions(params);
    } catch (Exception e) {
      log.error("Failed to fetch transactions", e);
      throw new TransfersFetchException(e);
    }
  }

  List<FundingRecord> getThalexTransactions(FundingRecordParamAll params) throws IOException {
    Long from = params.getStartTime() != null ? params.getStartTime().getTime() : null;
    Long to = params.getEndTime() != null ? params.getEndTime().getTime() : null;

    List<ThalexTransactionDto> transactions = FetchUtil.fetchAllPaginated(
        bookmark -> getThalexTransactions(from, to, bookmark),
        ThalexTransactionsDto::getTransactions,
        ThalexTransactionsDto::getBookmark
    );

    return transactions.stream()
        .map(ThalexAdapters::toFundingRecord)
        .collect(Collectors.toList());
  }

  List<FundingRecord> getFundingRecords(List<ThalexDeposit> deposits) {
    return deposits.stream()
        .map(ThalexAdapters::toFundingRecord).collect(Collectors.toList());
  }

  public List<ThalexDailyMarkDto> getThalexDailyMarkHistory(Integer limit, Long from, Long to) throws IOException {
    ThalexDailyMarkHistoryResult response = getThalexDailyMarkHistoryRaw(limit, from, to);
    return response.getDailyMarks();
  }
}
