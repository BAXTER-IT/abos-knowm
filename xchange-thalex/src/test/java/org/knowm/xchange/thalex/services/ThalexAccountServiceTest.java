package org.knowm.xchange.thalex.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.params.FundingRecordParamAll;
import org.knowm.xchange.thalex.ThalexExchange;
import org.knowm.xchange.thalex.dto.ThalexCash;
import org.knowm.xchange.thalex.dto.account.ThalexAccountSummaryDto;
import org.knowm.xchange.thalex.dto.account.ThalexCryptoDeposits;
import org.knowm.xchange.thalex.dto.account.ThalexDeposit;
import org.knowm.xchange.thalex.dto.account.ThalexPortfolioDto;
import org.knowm.xchange.thalex.dto.account.ThalexWithdrawal;
import org.knowm.xchange.thalex.dto.enums.ThalexDepositStatus;
import org.knowm.xchange.thalex.dto.enums.ThalexWithdrawalState;
import org.knowm.xchange.thalex.exceptions.TransfersFetchException;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ThalexAccountServiceTest {

  @InjectMocks
  private ThalexExchange exchange;
  private ThalexAccountService service;

  @Before
  public void setup() {
    exchange = new ThalexExchange() {
      @Override
      protected void initServices() {
        accountService = new ThalexAccountService(this);
      }
    };
    exchange.applySpecification(exchange.getDefaultExchangeSpecification());
    service = new ThalexAccountService(exchange);
  }

  @Test
  public void testGetAccountInfo() throws IOException {
    ThalexAccountSummaryDto mockSummary = ThalexAccountSummaryDto.builder()
        .cash(Collections.singletonList(
            ThalexCash.builder()
                .currency(new Currency("USD"))
                .balance(new BigDecimal("1000"))
                .transactable(true)
                .build()
        ))
        .build();

    ThalexPortfolioDto mockPortfolio = ThalexPortfolioDto.builder()
        .position(new BigDecimal("1"))
        .averagePrice(new BigDecimal("30000"))
        .unrealisedPnl(new BigDecimal("100"))
        .build();

    ThalexAccountService spy = spy(service);
    Mockito.doReturn(mockSummary).when(spy).getThalexBalances();
    Mockito.doReturn(Collections.singletonList(mockPortfolio)).when(spy).getThalexPortfolio();

    AccountInfo info = spy.getAccountInfo();

    assertNotNull(info);
    assertEquals(1, info.getWallets().size());
    assertEquals(1, info.getOpenPositions().size());
    assertEquals("derivatives", info.getWallets().get("derivatives").getId());
  }

  @Test
  public void testGetWithdrawHistory() throws IOException {
    ThalexWithdrawal withdrawal = ThalexWithdrawal.builder()
        .currency(new Currency("USDT"))
        .amount(new BigDecimal("100"))
        .transactionHash("0xabc")
        .createTime(Instant.now())
        .targetAddress("addr")
        .fee(BigDecimal.ONE)
        .state(ThalexWithdrawalState.EXECUTED)
        .remark("withdraw test")
        .build();

    ThalexAccountService spy = spy(service);
    Mockito.doReturn(Collections.singletonList(withdrawal)).when(spy).getThalexCryptoWithdrawals();

    List<FundingRecord> result = spy.getWithdrawHistory(null);

    assertEquals(1, result.size());
    assertEquals(new Currency("USDT"), result.get(0).getCurrency());
    assertEquals(FundingRecord.Type.WITHDRAWAL, result.get(0).getType());
  }

  @Test
  public void testGetDepositHistory() throws IOException {
    ThalexDeposit deposit1 = ThalexDeposit.builder()
        .currency(new Currency("BTC"))
        .amount(new BigDecimal("0.05"))
        .transactionTimestamp(Instant.now())
        .transactionHash("txhash1")
        .status(ThalexDepositStatus.CONFIRMED)
        .build();

    ThalexDeposit deposit2 = ThalexDeposit.builder()
        .currency(new Currency("BTC"))
        .amount(new BigDecimal("0.01"))
        .transactionTimestamp(Instant.now())
        .transactionHash("txhash2")
        .status(ThalexDepositStatus.UNCONFIRMED)
        .build();

    ThalexCryptoDeposits deposits = ThalexCryptoDeposits.builder()
        .confirmed(Collections.singletonList(deposit1))
        .unconfirmed(Collections.singletonList(deposit2))
        .build();

    ThalexAccountService spy = spy(service);
    Mockito.doReturn(deposits).when(spy).getThalexCryptoDeposits();

    List<FundingRecord> result = spy.getDepositHistory(null);

    assertEquals(2, result.size());
    assertEquals("txhash1", result.get(0).getBlockchainTransactionHash());
    assertEquals("txhash2", result.get(1).getBlockchainTransactionHash());
  }


  @Test(expected = TransfersFetchException.class)
  public void testGetWalletTransferHistory_exception() throws IOException {
    ThalexAccountService spy = spy(service);
    Mockito.doThrow(new RuntimeException("fail")).when(spy).getThalexTransactions(Mockito.any());

    spy.getWalletTransferHistory(FundingRecordParamAll.builder()
        .build());
  }
}
