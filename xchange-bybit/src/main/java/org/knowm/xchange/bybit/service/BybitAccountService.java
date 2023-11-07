package org.knowm.xchange.bybit.service;

import static org.knowm.xchange.bybit.BybitAdapters.adaptBybitDepositRecords;
import static org.knowm.xchange.bybit.BybitAdapters.adaptBybitInternalDepositRecords;
import static org.knowm.xchange.bybit.BybitAdapters.adaptBybitInternalTransfers;
import static org.knowm.xchange.bybit.BybitAdapters.adaptBybitWithdrawRecords;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.bybit.BybitAdapters;
import org.knowm.xchange.bybit.dto.BybitCategory;
import org.knowm.xchange.bybit.dto.account.BybitDepositRecordsResponse;
import org.knowm.xchange.bybit.dto.account.BybitInternalDepositRecordsResponse;
import org.knowm.xchange.bybit.dto.account.BybitTransactionLogResponse;
import org.knowm.xchange.bybit.dto.account.BybitTransactionLogResponse.BybitTransactionLog;
import org.knowm.xchange.bybit.dto.account.BybitTransfersResponse;
import org.knowm.xchange.bybit.dto.account.BybitWithdrawRecordsResponse;
import org.knowm.xchange.bybit.dto.account.BybitWithdrawRecordsResponse.BybitWithdrawRecord.BybitWithdrawType;
import org.knowm.xchange.bybit.dto.account.walletbalance.BybitAccountType;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.Wallet.WalletFeature;
import org.knowm.xchange.dto.account.params.FundingRecordParamAll;
import org.knowm.xchange.service.account.AccountService;

public class BybitAccountService extends BybitAccountServiceRaw implements AccountService {

  public static final int SEVEN_DAYS_IN_MILLIS = 7 * 24 * 60 * 60 * 1000;
  private static final Integer MAX_PAGINATION_LIMIT = 50;
  private final BybitAccountType accountType;

  public BybitAccountService(Exchange exchange, BybitAccountType accountType) {
    super(exchange);
    this.accountType = accountType;
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    List<Wallet> wallets = new ArrayList<>();

    if(accountType == BybitAccountType.UNIFIED){
      wallets.add(BybitAdapters.adaptBybitBalances(getAllCoinsBalance(BybitAccountType.UNIFIED, null, null, false).getResult(),
          Sets.newHashSet(WalletFeature.MARGIN_TRADING, WalletFeature.TRADING, WalletFeature.FUTURES_TRADING, WalletFeature.OPTIONS_TRADING)));
    } else if(accountType == BybitAccountType.CLASSIC) {
      wallets.add(BybitAdapters.adaptBybitBalances(getAllCoinsBalance(BybitAccountType.SPOT, null, null, false).getResult(),
          Sets.newHashSet(WalletFeature.TRADING, WalletFeature.MARGIN_TRADING)));
    }

    return new AccountInfo(wallets);
  }

  @Override
  public AccountInfo getSubAccountInfo(String subAccountId) throws IOException {
    List<Wallet> wallets = new ArrayList<>();

    if(accountType == BybitAccountType.UNIFIED){
      wallets.add(BybitAdapters.adaptBybitBalances(getAllCoinsBalance(BybitAccountType.UNIFIED, subAccountId, null, false).getResult(),
          Sets.newHashSet(WalletFeature.MARGIN_TRADING, WalletFeature.TRADING, WalletFeature.FUTURES_TRADING, WalletFeature.OPTIONS_TRADING)));
    } else if(accountType == BybitAccountType.CLASSIC) {
      wallets.add(BybitAdapters.adaptBybitBalances(getAllCoinsBalance(BybitAccountType.SPOT, subAccountId, null, false).getResult(),
          Sets.newHashSet(WalletFeature.TRADING, WalletFeature.MARGIN_TRADING)));
    }

    return new AccountInfo(wallets);
  }

  @Override
  public List<FundingRecord> getInternalTransferHistory(FundingRecordParamAll params) throws IOException {

    List<FundingRecord> fundingRecordList = new ArrayList<>();

    BybitTransfersResponse res = getBybitUniversalTransfers(
        params.getTransferId(),
        params.getCurrency(),
        BybitAdapters.convertToBybitStatus(params.getStatus()),
        params.getStartTime(),
        params.getEndTime(),
        (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
        null
    ).getResult();

    fundingRecordList.addAll(BybitAdapters.adaptBybitUniversalTransfers(res.getInternalTransfers()));

    if(params.isUsePagination()){
      String nextPageCursor = res.getNextPageCursor();

      while (nextPageCursor != null && !nextPageCursor.isEmpty()) {
        res = getBybitUniversalTransfers(
            params.getTransferId(),
            params.getCurrency(),
            BybitAdapters.convertToBybitStatus(params.getStatus()),
            params.getStartTime(),
            params.getEndTime(),
            (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
            res.getNextPageCursor()
        ).getResult();

        fundingRecordList.addAll(BybitAdapters.adaptBybitUniversalTransfers(res.getInternalTransfers()));
        nextPageCursor = res.getNextPageCursor();
      }
    }

    return fundingRecordList;
  }

  @Override
  public List<FundingRecord> getWalletTransferHistory(FundingRecordParamAll params)
      throws IOException {
    List<FundingRecord> fundingRecordList = new ArrayList<>();

    BybitTransfersResponse res = getBybitInternalTransfers(
        params.getTransferId(),
        params.getCurrency(),
        BybitAdapters.convertToBybitStatus(params.getStatus()),
        params.getStartTime(),
        params.getEndTime(),
        (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
        null
    ).getResult();

    fundingRecordList.addAll(adaptBybitInternalTransfers(res.getInternalTransfers()));

    if(params.isUsePagination()){
      String nextPageCursor = res.getNextPageCursor();

      while (nextPageCursor != null && !nextPageCursor.isEmpty()) {
        res = getBybitInternalTransfers(
            params.getTransferId(),
            params.getCurrency(),
            BybitAdapters.convertToBybitStatus(params.getStatus()),
            params.getStartTime(),
            params.getEndTime(),
            (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
            res.getNextPageCursor()
        ).getResult();

        fundingRecordList.addAll(adaptBybitInternalTransfers(res.getInternalTransfers()));
        nextPageCursor = res.getNextPageCursor();
      }
    }

    return fundingRecordList;
  }

  @Override
  public List<FundingRecord> getWithdrawHistory(FundingRecordParamAll params) throws IOException {
    List<FundingRecord> fundingRecordList = new ArrayList<>();

    BybitWithdrawRecordsResponse res = getBybitWithdrawRecords(
        params.getTransferId(),
        params.getCurrency(),
        BybitWithdrawType.ALL,
        params.getStartTime(),
        params.getEndTime(),
        (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
        null
    ).getResult();

    fundingRecordList.addAll(adaptBybitWithdrawRecords(res.getRows()));

    if(params.isUsePagination()){
      String nextPageCursor = res.getNextPageCursor();

      while (nextPageCursor != null && !nextPageCursor.isEmpty()) {
        res = getBybitWithdrawRecords(
            params.getTransferId(),
            params.getCurrency(),
            BybitWithdrawType.ALL,
            params.getStartTime(),
            params.getEndTime(),
            (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
            res.getNextPageCursor()
        ).getResult();

        fundingRecordList.addAll(adaptBybitWithdrawRecords(res.getRows()));
        nextPageCursor = res.getNextPageCursor();
      }
    }

    return fundingRecordList;
  }

  @Override
  public List<FundingRecord> getSubAccountDepositHistory(FundingRecordParamAll params)
      throws IOException {

    if (params.getSubAccountId() == null) {
      throw new IllegalArgumentException("Sub account id is required");
    }

    List<FundingRecord> fundingRecordList = new ArrayList<>();

    BybitDepositRecordsResponse res = getBybitSubAccountDepositRecords(
        params.getSubAccountId(),
        params.getCurrency(),
        params.getStartTime(),
        params.getEndTime(),
        (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
        null
    ).getResult();

    fundingRecordList.addAll(adaptBybitDepositRecords(res.getRows()));

    if(params.isUsePagination()){
      String nextPageCursor = res.getNextPageCursor();
      while (nextPageCursor != null && !nextPageCursor.isEmpty()) {
        res = getBybitSubAccountDepositRecords(
            params.getSubAccountId(),
            params.getCurrency(),
            params.getStartTime(),
            params.getEndTime(),
            (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
            res.getNextPageCursor()
        ).getResult();

        fundingRecordList.addAll(adaptBybitDepositRecords(res.getRows()));
        nextPageCursor = res.getNextPageCursor();
      }
    }

    return fundingRecordList;
  }

  @Override
  public List<FundingRecord> getDepositHistory(FundingRecordParamAll params) throws IOException {

    BybitDepositRecordsResponse res = getBybitDepositRecords(
        params.getCurrency(),
        params.getStartTime(),
        params.getEndTime(),
        (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
        null
    ).getResult();

    List<FundingRecord> fundingRecordList = new ArrayList<>();

    BybitInternalDepositRecordsResponse internalRes = getBybitInternalDepositRecords(
        params.getCurrency(),
        params.getStartTime(),
        params.getEndTime(),
        (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
        null
    ).getResult();

    fundingRecordList.addAll(adaptBybitDepositRecords(res.getRows()));
    fundingRecordList.addAll(adaptBybitInternalDepositRecords(internalRes.getRows()));

    if(params.isUsePagination()){
      // Make calls to main deposit history
      String nextPageCursor = res.getNextPageCursor();

      while (nextPageCursor != null && !nextPageCursor.isEmpty()) {

        res = getBybitDepositRecords(
            params.getCurrency(),
            params.getStartTime(),
            params.getEndTime(),
            (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
            res.getNextPageCursor()
        ).getResult();

        fundingRecordList.addAll(adaptBybitDepositRecords(res.getRows()));
        nextPageCursor = res.getNextPageCursor();
      }

      // Make calls to internal deposit history
      nextPageCursor = internalRes.getNextPageCursor();

      while (nextPageCursor != null && !nextPageCursor.isEmpty()) {

        internalRes = getBybitInternalDepositRecords(
            params.getCurrency(),
            params.getStartTime(),
            params.getEndTime(),
            (params.getLimit() == null) ? MAX_PAGINATION_LIMIT : params.getLimit(), // 50 is the maximum
            internalRes.getNextPageCursor()
        ).getResult();

        fundingRecordList.addAll(adaptBybitInternalDepositRecords(internalRes.getRows()));
        nextPageCursor = internalRes.getNextPageCursor();
      }
    }

    return fundingRecordList;
  }

  @Override
  public List<FundingRecord> getLedger(FundingRecordParamAll params) throws IOException {
    List<BybitTransactionLog> transactionLogs = new ArrayList<>();
    Long fromMillis = params.getStartTime() == null ? null : params.getStartTime().getTime();
    Long toMillis = params.getEndTime() == null ? null : params.getEndTime().getTime();

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
        transactionLogs.addAll(
            getPaginatedLedger(params.getCurrency(), newFrom, newTo, params.getLimit()));
      }
    } else {
      transactionLogs.addAll(
          getPaginatedLedger(params.getCurrency(), fromMillis, toMillis, params.getLimit()));
    }

    return BybitAdapters.adaptBybitLedger(transactionLogs);
  }

  private List<BybitTransactionLog> getPaginatedLedger(
      Currency currency, Long from, Long to, int limit) throws IOException {
    List<BybitTransactionLog> transactionLogs = new ArrayList<>();
    List<BybitTransactionLog> chunk;
    String nextPageCursor = null;

    do {
      BybitTransactionLogResponse result =
          getBybitLedger(accountType, null, currency, null, null, from, to, limit, nextPageCursor)
              .getResult();

      chunk = result.getList();
      transactionLogs.addAll(chunk);

      if (chunk.size() < limit) {
        break;
      }

      nextPageCursor = result.getNextPageCursor();
    } while (nextPageCursor != null && !nextPageCursor.isEmpty());

    return transactionLogs;
  }
}
