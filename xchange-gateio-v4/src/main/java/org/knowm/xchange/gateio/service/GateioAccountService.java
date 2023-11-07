package org.knowm.xchange.gateio.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.params.FundingRecordParamAll;
import org.knowm.xchange.gateio.GateioAdapters;
import org.knowm.xchange.gateio.GateioErrorAdapter;
import org.knowm.xchange.gateio.GateioExchange;
import org.knowm.xchange.gateio.dto.GateioException;
import org.knowm.xchange.gateio.dto.account.GateioCurrencyBalance;
import org.knowm.xchange.gateio.dto.account.GateioSubAccountBalance;
import org.knowm.xchange.gateio.dto.account.GateioWithdrawalRecord;
import org.knowm.xchange.gateio.dto.account.GateioWithdrawalRequest;
import org.knowm.xchange.gateio.service.params.GateioWithdrawFundsParams;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.WithdrawFundsParams;

public class GateioAccountService extends GateioAccountServiceRaw implements AccountService {

  public GateioAccountService(GateioExchange exchange) {
    super(exchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {

    try {
      List<GateioCurrencyBalance> spotBalances = getSpotBalances(null);

      List<Balance> balances =
          spotBalances.stream()
              .map(
                  balance ->
                      new Balance.Builder()
                          .currency(balance.getCurrency())
                          .available(balance.getAvailable())
                          .frozen(balance.getLocked())
                          .build())
              .collect(Collectors.toList());

      Wallet wallet = Wallet.Builder.from(balances).id("spot").build();

      return new AccountInfo(wallet);

    } catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }

  @Override
  public AccountInfo getSubAccountInfo(String subAccountId) throws IOException {
    try {

      List<GateioSubAccountBalance> subAccountBalances = getSubAccountBalances(subAccountId);
      List<Balance> balances =
          subAccountBalances.stream()
              .filter(subAccountBalance -> subAccountBalance.getSubAccountId().equals(subAccountId))
              .map(GateioSubAccountBalance::getAvailable)
              .findFirst()
              .orElse(new ArrayList<>());

      Wallet wallet = Wallet.Builder.from(balances).id("spot").build();

      return new AccountInfo(subAccountId, wallet);
    } catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }

  @Override
  public List<FundingRecord> getInternalTransferHistory(FundingRecordParamAll params)
      throws IOException {
    try {
      return getSubAccountTransfers(params).stream()
          .map(GateioAdapters::toFundingRecords)
          .collect(Collectors.toList());
    } catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }

  @Override
  public List<FundingRecord> getWithdrawHistory(FundingRecordParamAll params) throws IOException {
    try {
      return getWithdrawals(params).stream()
          .map(GateioAdapters::toFundingRecords)
          .collect(Collectors.toList());
    } catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }

  @Override
  public List<FundingRecord> getDepositHistory(FundingRecordParamAll params) throws IOException {
    try {
      return getDeposits(params).stream()
          .map(GateioAdapters::toFundingRecords)
          .collect(Collectors.toList());
    } catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }

  @Override
  public List<FundingRecord> getLedger(FundingRecordParamAll params) throws IOException {
    try {
      return getAccountBookRecords(params).stream()
          .map(GateioAdapters::toFundingRecords)
          .collect(Collectors.toList());
    } catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }

  @Override
  public String withdrawFunds(WithdrawFundsParams params) throws IOException {
    Validate.isInstanceOf(GateioWithdrawFundsParams.class, params);
    GateioWithdrawFundsParams p = (GateioWithdrawFundsParams) params;

    GateioWithdrawalRequest gateioWithdrawalRequest = GateioAdapters.toGateioWithdrawalRequest(p);

    try {
      GateioWithdrawalRecord gateioWithdrawalRecord = withdraw(gateioWithdrawalRequest);
      return gateioWithdrawalRecord.getId();
    } catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }
}
