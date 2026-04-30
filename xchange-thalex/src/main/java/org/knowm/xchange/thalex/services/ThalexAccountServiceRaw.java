package org.knowm.xchange.thalex.services;

import java.io.IOException;
import java.util.List;
import org.knowm.xchange.thalex.ThalexExchange;
import org.knowm.xchange.thalex.dto.account.ThalexAccountSummaryDto;
import org.knowm.xchange.thalex.dto.account.ThalexCryptoDeposits;
import org.knowm.xchange.thalex.dto.account.ThalexDailyMarkHistoryResult;
import org.knowm.xchange.thalex.dto.account.ThalexPortfolioDto;
import org.knowm.xchange.thalex.dto.account.ThalexTransactionsDto;
import org.knowm.xchange.thalex.dto.account.ThalexWithdrawal;

public class ThalexAccountServiceRaw extends ThalexBaseService {

  public ThalexAccountServiceRaw(ThalexExchange exchange) {
    super(exchange);
  }

  public ThalexAccountSummaryDto getThalexBalances() throws IOException {
    return thalexAuthenticated.balances(thalexDigest).getResult();
  }

  public List<ThalexPortfolioDto> getThalexPortfolio() throws IOException {
    return thalexAuthenticated.portfolio(thalexDigest).getResult();
  }

  protected ThalexTransactionsDto getThalexTransactions(Long from, Long to,
      String bookmark) throws IOException {
    return getThalexTransactions(null, from, to, bookmark);
  }

  protected ThalexTransactionsDto getThalexTransactions(Integer limit, Long from, Long to,
      String bookmark) throws IOException {
    return thalexAuthenticated.transactions(thalexDigest, limit, from, to, bookmark).getResult();
  }

  protected ThalexCryptoDeposits getThalexCryptoDeposits() throws IOException {
    return thalexAuthenticated.cryptoDeposits(thalexDigest).getResult();
  }

  protected List<ThalexWithdrawal> getThalexCryptoWithdrawals() throws IOException {
    return thalexAuthenticated.cryptoWithdrawals(thalexDigest).getResult();
  }

  protected ThalexDailyMarkHistoryResult getThalexDailyMarkHistoryRaw(Integer limit, Long from, Long to) throws IOException {
    return thalexAuthenticated.dailyMarkHistory(thalexDigest,  limit, from, to).getResult();
  }
}
