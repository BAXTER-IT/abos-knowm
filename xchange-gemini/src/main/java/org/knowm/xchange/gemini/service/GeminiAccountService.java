package org.knowm.xchange.gemini.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Fee;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Type;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.params.FundingRecordParamAll;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.gemini.GeminiAdapters;
import org.knowm.xchange.gemini.GeminiExchange;
import org.knowm.xchange.gemini.dto.account.GeminiBalancesResponse;
import org.knowm.xchange.gemini.dto.account.GeminiDepositAddressResponse;
import org.knowm.xchange.gemini.dto.account.GeminiPositionsResponse;
import org.knowm.xchange.gemini.dto.account.GeminiTrailingVolumeResponse;
import org.knowm.xchange.gemini.dto.account.GeminiTransaction;
import org.knowm.xchange.gemini.dto.account.GeminiTransactionResponse;
import org.knowm.xchange.gemini.dto.account.GeminiTransferResponse;
import org.knowm.xchange.gemini.exceptions.GeminiException;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.DefaultWithdrawFundsParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamCurrency;
import org.knowm.xchange.service.trade.params.TradeHistoryParamLimit;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsTimeSpan;
import org.knowm.xchange.service.trade.params.WithdrawFundsParams;

public class GeminiAccountService extends GeminiAccountServiceRaw implements AccountService {

  public static final int FUNDING_HISTORY_LIMIT_MAX = 50;

  public GeminiAccountService(GeminiExchange exchange) {
    super(exchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    List<GeminiBalancesResponse> balancesResponses = getGeminiBalances();
    Wallet wallet = GeminiAdapters.adaptWallet(balancesResponses);
    List<GeminiPositionsResponse> positionResponses = getGeminiPositions();
    List<OpenPosition> openPositions = GeminiAdapters.adaptPositions(positionResponses);
    return new AccountInfo(Collections.singletonList(wallet), openPositions);
  }

  @Override
  public String withdrawFunds(Currency currency, BigDecimal amount, String address)
      throws IOException {
    return withdraw(currency, amount, address);
  }

  @Override
  public String withdrawFunds(WithdrawFundsParams params) throws IOException {
    if (params instanceof DefaultWithdrawFundsParams) {
      DefaultWithdrawFundsParams defaultParams = (DefaultWithdrawFundsParams) params;
      return withdrawFunds(
          defaultParams.getCurrency(), defaultParams.getAmount(), defaultParams.getAddress());
    }
    throw new IllegalStateException("Don't know how to withdraw: " + params);
  }

  /**
   * This will result in a new address being created each time, and is severely rate-limited
   */
  @Override
  public String requestDepositAddress(Currency currency, String... arguments) throws IOException {
    GeminiDepositAddressResponse response = super.requestDepositAddressRaw(currency);
    return response.getAddress();
  }

  @Override
  public List<FundingRecord> getFundingHistory(TradeHistoryParams params)
      throws IOException {
    String currency = null;
    Long from = null;
    Integer userLimit = null; // null = fetch all

    if (params instanceof TradeHistoryParamCurrency) {
      TradeHistoryParamCurrency paramsCurrency = (TradeHistoryParamCurrency) params;
      if (paramsCurrency.getCurrency() != null) {
        currency = paramsCurrency.getCurrency().getCurrencyCode();
      }
    }
    if (params instanceof TradeHistoryParamLimit) {
      TradeHistoryParamLimit paramsLimit = (TradeHistoryParamLimit) params;
      userLimit = paramsLimit.getLimit();
    }

    if (params instanceof TradeHistoryParamsTimeSpan) {
      TradeHistoryParamsTimeSpan paramsTimeSpan = (TradeHistoryParamsTimeSpan) params;
      if (paramsTimeSpan.getStartTime() != null) {
        from = paramsTimeSpan.getStartTime().getTime();
      }
    }

    List<FundingRecord> results = new ArrayList<>();
    Long currentFrom = from;

    while (true) {
      int remaining = (userLimit != null) ? userLimit - results.size() : FUNDING_HISTORY_LIMIT_MAX;
      if (userLimit != null && remaining <= 0) {
        break;
      }

      int batchSize = Math.min(FUNDING_HISTORY_LIMIT_MAX, remaining);
      List<GeminiTransferResponse> batch = new ArrayList<>();
      try {

        batch = getGeminiTransfers(currency, currentFrom, batchSize);

        if (batch.isEmpty()) {
          break;
        }
      } catch (GeminiException e) {
        String errorMessage = e.getMessage();
        if (errorMessage.contains("Your account is rate-limited")) {
          Pattern pattern = Pattern.compile("\\d+");
          Matcher matcher = pattern.matcher(errorMessage);

          if (matcher.find()) {
            String numberStr = matcher.group();
            int duration = Integer.parseInt(numberStr) + 20;
            try {
              // Need this as the paginated endpoint is rate-limited to 1 request per 5 seconds...
              Thread.sleep(duration);
            } catch (InterruptedException ex) {
              throw new GeminiException("Rate-limiting errored out.");
            }
            continue;
          }
        } else {
          throw e;
        }
      }

      for (GeminiTransferResponse transfer : batch) {
        results.add(GeminiAdapters.adaptTransfer(transfer));
      }

      if (batch.size() < batchSize) {
        break;
      }

      long lastTimestamp = batch.get(0).getTimestampms();
      currentFrom = lastTimestamp + 1;
    }

    return results;
  }

  @Override
  public List<FundingRecord> getDepositHistory(FundingRecordParamAll params) throws IOException {
    TradeHistoryParamsAll tradeHistoryParamsAll = new TradeHistoryParamsAll();
    tradeHistoryParamsAll.setCurrency(params.getCurrency());
    tradeHistoryParamsAll.setLimit(params.getLimit());
    tradeHistoryParamsAll.setStartTime(params.getStartTime());

    return getFundingHistory(tradeHistoryParamsAll).stream()
        .filter(fundingRecord -> Type.DEPOSIT.equals(fundingRecord.getType())).collect(
            Collectors.toList());
  }

  @Override
  public List<FundingRecord> getWithdrawHistory(FundingRecordParamAll params) throws IOException {
    TradeHistoryParamsAll tradeHistoryParamsAll = new TradeHistoryParamsAll();
    tradeHistoryParamsAll.setCurrency(params.getCurrency());
    tradeHistoryParamsAll.setLimit(params.getLimit());
    tradeHistoryParamsAll.setStartTime(params.getStartTime());

    return getFundingHistory(tradeHistoryParamsAll).stream()
        .filter(fundingRecord -> Type.WITHDRAWAL.equals(fundingRecord.getType())).collect(
            Collectors.toList());
  }

  @Override
  public List<FundingRecord> getWalletTransferHistory(FundingRecordParamAll params)
      throws IOException {
    return getLedger(params).stream().filter(transaction -> Type.INTERNAL_WALLET_TRANSFER.equals(transaction.getType())).collect(
        Collectors.toList());
  }

  @Override
  public List<FundingRecord> getLedger(FundingRecordParamAll params) throws IOException {
    Long fromNanos = null;
    Integer limit = null;
    if (params.getStartTime() != null) {
      fromNanos = params.getStartTime().getTime() * 1000;
    }
    if (params.getLimit() != null) {
      limit = params.getLimit();
    }
    GeminiTransactionResponse geminiTransactions = getGeminiTransactions(fromNanos, limit, null);
    List<GeminiTransaction> result = new ArrayList<>(geminiTransactions.getResults());
    String continuationToken = geminiTransactions.getContinuationToken();
    while (continuationToken != null) {
      GeminiTransactionResponse batch = getGeminiTransactions(null, limit,
          continuationToken);
      result.addAll(batch.getResults());
      continuationToken = batch.getContinuationToken();
    }

    return GeminiAdapters.adaptTransactions(result);
  }

  @Override
  public TradeHistoryParams createFundingHistoryParams() {
    throw new NotAvailableFromExchangeException();
  }

  @Override
  public Map<Instrument, Fee> getDynamicTradingFeesByInstrument(String... category) throws IOException {
    GeminiTrailingVolumeResponse volumes = Get30DayTrailingVolumeDescription();
    return GeminiAdapters.AdaptDynamicTradingFees(volumes, allCurrencyPairs);
  }
}
