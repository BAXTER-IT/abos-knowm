package org.knowm.xchange.bitget.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.bitget.BitgetAdapters;
import org.knowm.xchange.bitget.BitgetErrorAdapter;
import org.knowm.xchange.bitget.BitgetExchange;
import org.knowm.xchange.bitget.dto.BitgetException;
import org.knowm.xchange.bitget.dto.account.BitgetBalanceDto;
import org.knowm.xchange.bitget.dto.account.BitgetDepositWithdrawRecordDto;
import org.knowm.xchange.bitget.dto.account.BitgetSubBalanceDto;
import org.knowm.xchange.bitget.dto.account.params.BitgetMainSubTransferHistoryParams;
import org.knowm.xchange.bitget.dto.account.params.BitgetTransferHistoryParams;
import org.knowm.xchange.bitget.service.params.BitgetFundingHistoryParams;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.params.FundingRecordParamAll;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;

@Slf4j
public class BitgetAccountService extends BitgetAccountServiceRaw implements AccountService {

  public BitgetAccountService(BitgetExchange exchange) {
    super(exchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    try {
      List<BitgetBalanceDto> spotBalances = getBitgetBalances(null);
      Wallet wallet = BitgetAdapters.toWallet(spotBalances);
      return new AccountInfo(wallet);

    } catch (BitgetException e) {
      throw BitgetErrorAdapter.adapt(e);
    }
  }

  @Override
  public AccountInfo getSubAccountInfo(String subAccountId) throws IOException {
    try {
      BitgetSubBalanceDto bitgetSubBalanceDto =
          getSubBitgetBalances().stream()
              .filter(subBalance -> subBalance.getUserId().equals(subAccountId))
              .findFirst()
              .orElse(BitgetSubBalanceDto.builder().balances(Collections.emptyList()).build());
      if (bitgetSubBalanceDto.getBalances().isEmpty()) {
        log.warn("No balances found for sub account {}", subAccountId);
      }
      Wallet wallet = BitgetAdapters.toWallet(bitgetSubBalanceDto.getBalances());
      return new AccountInfo(wallet);
    } catch (BitgetException e) {
      throw BitgetErrorAdapter.adapt(e);
    }
  }

  @Override
  public TradeHistoryParams createFundingHistoryParams() {
    return BitgetFundingHistoryParams.builder().build();
  }

  @Override
  public List<FundingRecord> getFundingHistory(TradeHistoryParams params) throws IOException {
    // return withdrawals and deposits combined
    return Stream.of(getBitgetDepositRecords(params), getBitgetWithdrawRecords(params))
        .flatMap(List::stream)
        .map(BitgetAdapters::toFundingRecord)
        .collect(Collectors.toList());
  }

  @Override
  public List<FundingRecord> getWalletTransferHistory(FundingRecordParamAll params)
      throws IOException {

    BitgetMainSubTransferHistoryParams bitgetMainSubTransferHistoryParams =
        BitgetAdapters.toMainSubTransferHistoryParams(params);

    return getBitgetMainSubTransferRecords(bitgetMainSubTransferHistoryParams).stream()
        .map(BitgetAdapters::toFundingRecord)
        .collect(Collectors.toList());
  }

  @Override
  public List<FundingRecord> getInternalTransferHistory(FundingRecordParamAll params)
      throws IOException {
    BitgetTransferHistoryParams bitgetTransferHistoryParams =
        BitgetAdapters.toTransferHistoryParams(params);

    return getBitgetTransferRecords(bitgetTransferHistoryParams).stream()
        .map(BitgetAdapters::toFundingRecord)
        .collect(Collectors.toList());
  }

  @Override
  public List<FundingRecord> getWithdrawHistory(FundingRecordParamAll params) throws IOException {

    TradeHistoryParamsAll tradeHistoryParamsAll = BitgetAdapters.toTradeHistoryParamsAll(params);
    return getBitgetWithdrawRecords(tradeHistoryParamsAll).stream()
        .map(BitgetAdapters::toFundingRecord)
        .collect(Collectors.toList());
  }

  @Override
  public List<FundingRecord> getDepositHistory(FundingRecordParamAll params) throws IOException {
    TradeHistoryParamsAll tradeHistoryParams = BitgetAdapters.toTradeHistoryParamsAll(params);
    List<BitgetDepositWithdrawRecordDto> bitgetDepositRecords =
        getBitgetDepositRecords(tradeHistoryParams);
    return bitgetDepositRecords.stream()
        .map(BitgetAdapters::toFundingRecord)
        .collect(Collectors.toList());
  }

  @Override
  public List<FundingRecord> getSubAccountDepositHistory(FundingRecordParamAll params)
      throws IOException {
    TradeHistoryParamsAll tradeHistoryParams = BitgetAdapters.toTradeHistoryParamsAll(params);
    List<BitgetDepositWithdrawRecordDto> records =
        getBitgetSubAccountDepositRecords(tradeHistoryParams);
    return records.stream().map(BitgetAdapters::toFundingRecord).collect(Collectors.toList());
  }

  @Override
  public List<FundingRecord> getLedger(FundingRecordParamAll params) throws IOException {
    return getInternalTransferHistory(params);
  }
}
