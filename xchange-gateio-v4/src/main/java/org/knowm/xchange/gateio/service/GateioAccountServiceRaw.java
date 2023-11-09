package org.knowm.xchange.gateio.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.params.FundingRecordParamAll;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.gateio.GateioAdapters;
import org.knowm.xchange.gateio.GateioErrorAdapter;
import org.knowm.xchange.gateio.GateioExchange;
import org.knowm.xchange.gateio.dto.GateioException;
import org.knowm.xchange.gateio.dto.account.GateioAccountBookRecord;
import org.knowm.xchange.gateio.dto.account.GateioAddressRecord;
import org.knowm.xchange.gateio.dto.account.GateioCurrencyBalance;
import org.knowm.xchange.gateio.dto.account.GateioDepositAddress;
import org.knowm.xchange.gateio.dto.account.GateioDepositRecord;
import org.knowm.xchange.gateio.dto.account.GateioSubAccountBalance;
import org.knowm.xchange.gateio.dto.account.GateioSubAccountTransfer;
import org.knowm.xchange.gateio.dto.account.GateioWithdrawStatus;
import org.knowm.xchange.gateio.dto.account.GateioWithdrawalRecord;
import org.knowm.xchange.gateio.dto.account.GateioWithdrawalRequest;
import org.knowm.xchange.gateio.service.params.GateioFundingHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamPaging;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsTimeSpan;

public class GateioAccountServiceRaw extends GateioBaseService {

  public static final int PAGINATION_LIMIT_MAX = 1000;
  public static final int THIRTY_DAYS_IN_SECONDS = 30 * 24 * 60 * 60;

  public GateioAccountServiceRaw(GateioExchange exchange) {
    super(exchange);
  }

  public GateioDepositAddress getDepositAddress(Currency currency) throws IOException {
    String currencyCode = currency == null ? null : currency.getCurrencyCode();

    try {
      return gateioV4Authenticated.getDepositAddress(
          apiKey, exchange.getNonceFactory(), gateioV4ParamsDigest, currencyCode);
    } catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }

  public List<GateioWithdrawStatus> getWithdrawStatus(Currency currency) throws IOException {
    String currencyCode = currency == null ? null : currency.getCurrencyCode();

    try {
      return gateioV4Authenticated.getWithdrawStatus(
          apiKey, exchange.getNonceFactory(), gateioV4ParamsDigest, currencyCode);
    } catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }

  public List<GateioCurrencyBalance> getSpotBalances(Currency currency) throws IOException {
    String currencyCode = currency == null ? null : currency.getCurrencyCode();
    return gateioV4Authenticated.getSpotAccounts(
        apiKey, exchange.getNonceFactory(), gateioV4ParamsDigest, currencyCode);
  }

  public List<GateioSubAccountBalance> getSubAccountBalances(String subAccountId)
      throws IOException {
    try {
      return gateioV4Authenticated.getSubAccountBalances(
          apiKey, exchange.getNonceFactory(), gateioV4ParamsDigest, subAccountId);
    } catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }

  public List<GateioWithdrawalRecord> getWithdrawals(FundingRecordParamAll params)
      throws IOException {
    String currency = params.getCurrency() != null ? params.getCurrency().toString() : null;
    Long from = params.getStartTime() != null ? params.getStartTime().getTime() / 1000 : null;
    Long to = params.getEndTime() != null ? params.getEndTime().getTime() / 1000 : null;
    int pageLength = params.getLimit() != null ? params.getLimit() : PAGINATION_LIMIT_MAX;
    if (params.isUsePagination()) {
      return getPaginatedWithdrawals(currency, from, to, pageLength);
    } else {
      return gateioV4Authenticated.getWithdrawals(
          apiKey,
          exchange.getNonceFactory(),
          gateioV4ParamsDigest,
          currency,
          from,
          to,
          pageLength,
          0);
    }
  }

  private List<GateioWithdrawalRecord> getPaginatedWithdrawals(
      String currency, Long from, Long to, int pageLength) throws IOException {
    List<GateioWithdrawalRecord> result = new ArrayList<>();
    List<GateioWithdrawalRecord> records;
    int pageOffset = 0;
    do {
      records =
          gateioV4Authenticated.getWithdrawals(
              apiKey,
              exchange.getNonceFactory(),
              gateioV4ParamsDigest,
              currency,
              from,
              to,
              pageLength,
              pageOffset);

      result.addAll(records);
      pageOffset += pageLength;
    } while (records.size() == pageLength);

    return result;
  }

  public List<GateioDepositRecord> getDeposits(FundingRecordParamAll params) throws IOException {
    String currency = params.getCurrency() != null ? params.getCurrency().toString() : null;
    Long from = params.getStartTime() != null ? params.getStartTime().getTime() / 1000 : null;
    Long to = params.getEndTime() != null ? params.getEndTime().getTime() / 1000 : null;
    int pageLength = params.getLimit() != null ? params.getLimit() : PAGINATION_LIMIT_MAX;
    if (params.isUsePagination()) {
      return getPaginatedDeposits(currency, from, to, pageLength);
    } else {
      return gateioV4Authenticated.getDeposits(
          apiKey,
          exchange.getNonceFactory(),
          gateioV4ParamsDigest,
          currency,
          from,
          to,
          pageLength,
          0);
    }
  }

  private List<GateioDepositRecord> getPaginatedDeposits(
      String currency, Long from, Long to, int pageLength) throws IOException {
    List<GateioDepositRecord> result = new ArrayList<>();
    List<GateioDepositRecord> records;
    int pageOffset = 0;
    do {
      records =
          gateioV4Authenticated.getDeposits(
              apiKey,
              exchange.getNonceFactory(),
              gateioV4ParamsDigest,
              currency,
              from,
              to,
              pageLength,
              pageOffset);

      result.addAll(records);
      pageOffset += pageLength;
    } while (records.size() == pageLength);

    return result;
  }

  public GateioWithdrawalRecord withdraw(GateioWithdrawalRequest gateioWithdrawalRequest)
      throws IOException {
    return gateioV4Authenticated.withdraw(
        apiKey, exchange.getNonceFactory(), gateioV4ParamsDigest, gateioWithdrawalRequest);
  }

  public List<GateioAddressRecord> getSavedAddresses(Currency currency) throws IOException {
    Validate.notNull(currency);
    return gateioV4Authenticated.getSavedAddresses(
        apiKey, exchange.getNonceFactory(), gateioV4ParamsDigest, currency.getCurrencyCode());
  }

  public List<GateioAccountBookRecord> getAccountBookRecords(TradeHistoryParams params)
      throws IOException {
    // get arguments
    Currency currency =
        params instanceof GateioFundingHistoryParams
            ? ((GateioFundingHistoryParams) params).getCurrency()
            : null;
    String currencyCode = currency != null ? currency.toString() : null;
    String type =
        params instanceof GateioFundingHistoryParams
            ? ((GateioFundingHistoryParams) params).getType()
            : null;
    Integer pageLength =
        params instanceof TradeHistoryParamPaging
            ? ((TradeHistoryParamPaging) params).getPageLength()
            : null;
    Integer pageNumber =
        params instanceof TradeHistoryParamPaging
            ? ((TradeHistoryParamPaging) params).getPageNumber()
            : null;
    Long from = null;
    Long to = null;
    if (params instanceof TradeHistoryParamsTimeSpan) {
      TradeHistoryParamsTimeSpan paramsTimeSpan = ((TradeHistoryParamsTimeSpan) params);
      from =
          paramsTimeSpan.getStartTime() != null
              ? paramsTimeSpan.getStartTime().getTime() / 1000
              : null;
      to =
          paramsTimeSpan.getEndTime() != null ? paramsTimeSpan.getEndTime().getTime() / 1000 : null;
    }

    return gateioV4Authenticated.getAccountBookRecords(
        apiKey,
        exchange.getNonceFactory(),
        gateioV4ParamsDigest,
        currencyCode,
        from,
        to,
        pageLength,
        pageNumber,
        type);
  }

  public List<GateioAccountBookRecord> getAccountBookRecords(FundingRecordParamAll params)
      throws IOException {
    Currency currency = params.getCurrency();
    Long fromSec = params.getStartTime() != null ? params.getStartTime().getTime() / 1000 : null;
    Long toSec = params.getEndTime() != null ? params.getEndTime().getTime() / 1000 : null;
    int pageLength = params.getLimit() != null ? params.getLimit() : PAGINATION_LIMIT_MAX;
    String type = GateioAdapters.adaptFundingRecordType(params.getType());
    List<GateioAccountBookRecord> result = new ArrayList<>();

    if (fromSec != null && toSec != null) {
      if (fromSec > toSec) {
        Long temp = fromSec;
        fromSec = toSec;
        toSec = temp;
      }
      int thirtyDaySpans = (int) Math.ceil((toSec - fromSec) / (double) THIRTY_DAYS_IN_SECONDS);
      for (int i = 0; i < thirtyDaySpans; i++) {
        long newFrom = fromSec + ((long) i * THIRTY_DAYS_IN_SECONDS);
        long newTo = Math.min(toSec, newFrom + THIRTY_DAYS_IN_SECONDS);
        if (params.isUsePagination()) {
          result.addAll(
              getPaginatedAccountBookRecords(currency, newFrom, newTo, pageLength, type));
        } else {
          result.addAll(
              gateioV4Authenticated.getAccountBookRecords(
                  apiKey,
                  exchange.getNonceFactory(),
                  gateioV4ParamsDigest,
                  currency.toString(),
                  newFrom,
                  newTo,
                  pageLength,
                  1,
                  type));
          break;
        }
      }
    } else {
      if (params.isUsePagination()) {
        result.addAll(
            getPaginatedAccountBookRecords(currency, fromSec, toSec, pageLength, type));
      } else {
        result.addAll(
            gateioV4Authenticated.getAccountBookRecords(
                apiKey,
                exchange.getNonceFactory(),
                gateioV4ParamsDigest,
                currency.toString(),
                fromSec,
                toSec,
                pageLength,
                1,
                type));
      }
    }

    return result;
  }

  private List<GateioAccountBookRecord> getPaginatedAccountBookRecords(
      Currency currency, Long from, Long to, int pageLength, String type) throws IOException {
    List<GateioAccountBookRecord> result = new ArrayList<>();
    List<GateioAccountBookRecord> ledgerResponse;
    int pageNumber = 1;
    do {
      ledgerResponse =
          gateioV4Authenticated.getAccountBookRecords(
              apiKey,
              exchange.getNonceFactory(),
              gateioV4ParamsDigest,
              currency.toString(),
              from,
              to,
              pageLength,
              pageNumber++,
              type);

      result.addAll(ledgerResponse);
    } while (ledgerResponse.size() == pageLength);

    return result;
  }

  public List<GateioSubAccountTransfer> getSubAccountTransfers(FundingRecordParamAll params)
      throws IOException {
    Long fromSec = params.getStartTime() != null ? params.getStartTime().getTime() / 1000 : null;
    Long toSec = params.getEndTime() != null ? params.getEndTime().getTime() / 1000 : null;
    int pageLength = params.getLimit() != null ? params.getLimit() : PAGINATION_LIMIT_MAX;

    List<GateioSubAccountTransfer> result = new ArrayList<>();

    if (fromSec != null && toSec != null) {
      if (fromSec > toSec) {
        Long temp = fromSec;
        fromSec = toSec;
        toSec = temp;
      }
      int thirtyDaySpans = (int) Math.ceil((toSec - fromSec) / (double) THIRTY_DAYS_IN_SECONDS);
      for (int i = 0; i < thirtyDaySpans; i++) {
        long newFrom = fromSec + ((long) i * THIRTY_DAYS_IN_SECONDS);
        long newTo = Math.min(toSec, newFrom + THIRTY_DAYS_IN_SECONDS);
        if (params.isUsePagination()) {
          result.addAll(
              getPaginatedSubAccountTransfers(
                  params.getSubAccountId(), newFrom, newTo, pageLength));
        } else {
          result.addAll(
              gateioV4Authenticated.getSubAccountTransfers(
                  apiKey,
                  exchange.getNonceFactory(),
                  gateioV4ParamsDigest,
                  params.getSubAccountId(),
                  newFrom,
                  newTo,
                  pageLength,
                  0));
          break;
        }
      }
    } else {
      if (params.isUsePagination()) {
        result.addAll(
            getPaginatedSubAccountTransfers(params.getSubAccountId(), fromSec, toSec, pageLength));
      } else {
        result.addAll(
            gateioV4Authenticated.getSubAccountTransfers(
                apiKey,
                exchange.getNonceFactory(),
                gateioV4ParamsDigest,
                params.getSubAccountId(),
                fromSec,
                toSec,
                pageLength,
                0));
      }
    }

    return result;
  }

  private List<GateioSubAccountTransfer> getPaginatedSubAccountTransfers(
      String subAccountId, Long from, Long to, int pageLength) throws IOException {
    List<GateioSubAccountTransfer> result = new ArrayList<>();
    List<GateioSubAccountTransfer> subAccountTransfers;
    int pageOffset = 0;
    do {
      subAccountTransfers =
          gateioV4Authenticated.getSubAccountTransfers(
              apiKey,
              exchange.getNonceFactory(),
              gateioV4ParamsDigest,
              subAccountId,
              from,
              to,
              pageLength,
              pageOffset);

      result.addAll(subAccountTransfers);
      pageOffset += pageLength;
    } while (subAccountTransfers.size() == pageLength);

    return result;
  }
}
