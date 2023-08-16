package org.knowm.xchange.coinbasepro;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinbasepro.dto.CoinbasePagedResponse;
import org.knowm.xchange.coinbasepro.dto.CoinbaseProTransfers;
import org.knowm.xchange.coinbasepro.dto.account.CoinbaseProAccount;
import org.knowm.xchange.coinbasepro.dto.account.CoinbaseProFundingHistoryParams;
import org.knowm.xchange.coinbasepro.dto.trade.CoinbaseProFill;
import org.knowm.xchange.coinbasepro.service.CoinbaseProAccountService;
import org.knowm.xchange.coinbasepro.service.CoinbaseProAccountServiceRaw;
import org.knowm.xchange.coinbasepro.service.CoinbaseProTradeServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.params.DefaultTradeHistoryParamInstrument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoinbaseProPrivateRawIntegration {

  private final Exchange exchange = CoinbaseProPrivateInit.getCoinbasePrivateInstance();
  private static final Logger LOG = LoggerFactory.getLogger(CoinbaseProPrivateIntegration.class);
  Instrument instrument = new CurrencyPair("BTC/USD");

  /**
   * AccountServiceRaw tests
   */
  @Test
  public void testCoinbaseAccountById() throws IOException {
    AccountInfo accountInfo = exchange.getAccountService().getAccountInfo();

    LOG.info(accountInfo.toString());
    assertThat(accountInfo.getWallet()).isNotNull();

    CoinbaseProAccountServiceRaw raw = (CoinbaseProAccountServiceRaw) exchange.getAccountService();

    CoinbaseProAccount[] accounts = raw.getCoinbaseProAccountInfo();
    assertThat(accounts).isNotEmpty();

    CoinbaseProAccount account = raw.getCoinbaseProAccountById(accounts[0].getId());

    LOG.info(account.toString());
    assertThat(account).isNotNull();
    assertThat(account.getId()).isNotNull();
    assertThat(account.getCurrency()).isNotNull();
    assertThat(account.getBalance()).isNotNull();
    assertThat(account.getAvailable()).isNotNull();
  }

  @Test
  public void testLedger() throws IOException {
    CoinbaseProAccountService service = (CoinbaseProAccountService) exchange.getAccountService();
    CoinbaseProAccount account = service.getCoinbaseProAccountInfo()[1];

    CoinbaseProFundingHistoryParams params = new CoinbaseProFundingHistoryParams();

    params.setTransactionId(account.getId());


    service.getLedgerWithPagination(params).forEach(coinbaseProLedgerDto -> {
      LOG.info(coinbaseProLedgerDto.toString());
      assertThat(coinbaseProLedgerDto).isNotNull();
      assertThat(coinbaseProLedgerDto.getId()).isNotNull();
      assertThat(coinbaseProLedgerDto.getAmount()).isInstanceOf(BigDecimal.class);
    });

  }

  /**
   * TradeServiceRaw tests
   */
  @Test
  public void testTradeHistoryRawData() throws IOException {
    CoinbaseProTradeServiceRaw raw = (CoinbaseProTradeServiceRaw) exchange.getTradeService();
    CoinbasePagedResponse<CoinbaseProFill> rawData = raw.getCoinbaseProFills(new DefaultTradeHistoryParamInstrument(instrument));

    rawData.forEach(coinbaseProFill -> {
      assertThat(coinbaseProFill).isNotNull();
      assertThat(coinbaseProFill.getTradeId()).isNotNull();
      assertThat(coinbaseProFill.getProductId()).isNotNull();
      assertThat(coinbaseProFill.getPrice()).isGreaterThan(BigDecimal.ZERO);
      assertThat(coinbaseProFill.getSize()).isGreaterThan(BigDecimal.ZERO);
      assertThat(coinbaseProFill.getFee()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
      assertThat(coinbaseProFill.getCreatedAt()).isNotNull();
      assertThat(coinbaseProFill.getLiquidity()).isNotNull();
      assertThat(coinbaseProFill.getOrderId()).isNotNull();
      assertThat(coinbaseProFill.getSide()).isNotNull();
    });
  }

  @Test
  public void testCoinbaseTransfers() throws IOException {
    CoinbaseProAccountServiceRaw raw = (CoinbaseProAccountServiceRaw) exchange.getAccountService();

    String btcAccountId = Arrays.stream(raw.getCoinbaseProAccountInfo()).filter(coinbaseProAccount -> coinbaseProAccount.getCurrency().equals("BTC")).findFirst().get().getId();

    CoinbaseProTransfers transfers =
        raw.getTransfersByAccountId(
            btcAccountId,
            null,
            Date.from(Instant.now()),
            100,
            null);

    transfers.forEach(coinbaseProTransfer -> {
      LOG.info(coinbaseProTransfer.toString());
      assertThat(coinbaseProTransfer).isNotNull();
      assertThat(coinbaseProTransfer.getId()).isNotNull();
      assertThat(coinbaseProTransfer.getType()).isInstanceOf(FundingRecord.Type.class);
      assertThat(coinbaseProTransfer.getCreatedAt()).isInstanceOf(Date.class);
      assertThat(coinbaseProTransfer.getCompletedAt()).isInstanceOf(Date.class);
      assertThat(coinbaseProTransfer.getAmount()).isGreaterThan(BigDecimal.ZERO);
    });

    CoinbaseProFundingHistoryParams fundingHistoryParams = (CoinbaseProFundingHistoryParams) exchange.getAccountService().createFundingHistoryParams();

    fundingHistoryParams.setLimit(50);
    fundingHistoryParams.setStartTime(Date.from(Instant.now()));
    fundingHistoryParams.setEndTime(Date.from(Instant.now()));
    fundingHistoryParams.setType(FundingRecord.Type.WITHDRAW);

    CoinbaseProAccountService accountService = (CoinbaseProAccountService) exchange.getAccountService();

    accountService.getTransfersWithPagination(fundingHistoryParams).getFundingRecords().forEach(fundingRecord -> {
      LOG.info(fundingRecord.toString());
      assertThat(fundingRecord).isNotNull();
      assertThat(fundingRecord.getDate()).isNotNull();
      assertThat(fundingRecord.getAmount()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
      assertThat(fundingRecord.getType()).isInstanceOf(FundingRecord.Type.class);
      assertThat(fundingRecord.getStatus()).isInstanceOf(FundingRecord.Status.class);
      assertThat(fundingRecord.getCurrency()).isNotNull();
    });
  }
}
