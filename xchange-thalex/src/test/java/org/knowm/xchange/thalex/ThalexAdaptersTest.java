package org.knowm.xchange.thalex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.derivative.CombinationsContract;
import org.knowm.xchange.derivative.CombinationsContract.Leg;
import org.knowm.xchange.derivative.FuturesContract;
import org.knowm.xchange.derivative.OptionsContract;
import org.knowm.xchange.derivative.OptionsContract.OptionType;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Status;
import org.knowm.xchange.dto.account.FundingRecord.Type;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.enums.MarketParticipant;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.thalex.dto.ThalexCash;
import org.knowm.xchange.thalex.dto.account.ThalexAccountSummaryDto;
import org.knowm.xchange.thalex.dto.account.ThalexDeposit;
import org.knowm.xchange.thalex.dto.account.ThalexPortfolioDto;
import org.knowm.xchange.thalex.dto.account.ThalexTransactionDto;
import org.knowm.xchange.thalex.dto.account.ThalexWithdrawal;
import org.knowm.xchange.thalex.dto.enums.ThalexDepositStatus;
import org.knowm.xchange.thalex.dto.enums.ThalexDirection;
import org.knowm.xchange.thalex.dto.enums.ThalexInstrumentType;
import org.knowm.xchange.thalex.dto.enums.ThalexMakerTaker;
import org.knowm.xchange.thalex.dto.enums.ThalexTransactionType;
import org.knowm.xchange.thalex.dto.enums.ThalexWithdrawalState;
import org.knowm.xchange.thalex.dto.marketdata.ThalexInstrumentDto;
import org.knowm.xchange.thalex.dto.marketdata.ThalexInstrumentLegDto;
import org.knowm.xchange.thalex.dto.trade.ThalexTradeDto;


public class ThalexAdaptersTest {

  @Test
  public void testToWallet() {
    ThalexCash cash = ThalexCash.builder()
        .currency(new Currency("USD"))
        .balance(new BigDecimal("1000.00"))
        .transactable(true)
        .build();
    ThalexAccountSummaryDto summaryDto = ThalexAccountSummaryDto.builder()
        .cash(Collections.singletonList(cash))
        .build();

    Wallet wallet = ThalexAdapters.toWallet(summaryDto);

    assertNotNull(wallet);
    assertEquals("derivatives", wallet.getId());
    assertEquals(1, wallet.getBalances().size());
    assertTrue(wallet.getFeatures().contains(Wallet.WalletFeature.FUTURES_TRADING));
  }

  @Test
  public void testToWallet_filterNonTransactable() {
    ThalexCash cash = ThalexCash.builder()
        .currency(new Currency("USD"))
        .balance(new BigDecimal("1000.00"))
        .transactable(true)
        .build();
    ThalexCash cashNonTransactable = ThalexCash.builder()
        .currency(new Currency("BTC"))
        .balance(new BigDecimal("1000.00"))
        .transactable(false)
        .build();

    List<ThalexCash> cashes = new ArrayList<>();
    cashes.add(cash);
    cashes.add(cashNonTransactable);

    ThalexAccountSummaryDto summaryDto = ThalexAccountSummaryDto.builder()
        .cash(cashes)
        .build();

    Wallet wallet = ThalexAdapters.toWallet(summaryDto);

    assertNotNull(wallet);
    assertEquals("derivatives", wallet.getId());
    assertEquals(1, wallet.getBalances().size());
    assertNotNull(wallet.getBalances().get(new Currency("USD")));
    assertNull(wallet.getBalances().get(new Currency("BTC")));
    assertTrue(wallet.getFeatures().contains(Wallet.WalletFeature.FUTURES_TRADING));
  }

  @Test
  public void testToBalance() {
    ThalexCash cash = ThalexCash.builder()
        .currency(new Currency("EUR"))
        .balance(new BigDecimal("500.50"))
        .build();

    Balance balance = ThalexAdapters.toBalance(cash);

    assertNotNull(balance);
    assertEquals(new Currency("EUR"), balance.getCurrency());
    assertEquals(new BigDecimal("500.50"), balance.getAvailable());
  }

  @Test
  public void testToDate_withValidInstant() {
    Instant now = Instant.now();
    Date date = ThalexAdapters.toDate(now);

    assertNotNull(date);
    assertEquals(Date.from(now), date);
  }

  @Test
  public void testToDate_withNullInstant() {
    Date date = ThalexAdapters.toDate(null);
    assertNull(date);
  }

  @Test
  public void testToUserTrade() {
    String instrumentName = "BTC-PERPETUAL";

    ThalexAdapters.addInstrumentToMap(
        ThalexInstrumentDto.builder().instrumentName(instrumentName)
            .type(ThalexInstrumentType.FUTURE).build()
    );

    ThalexTradeDto tradeDto = ThalexTradeDto.builder()
        .direction(ThalexDirection.BUY)
        .amount(new BigDecimal("0.5"))
        .instrumentName(instrumentName)
        .price(new BigDecimal("30000.00"))
        .time(Instant.parse("2025-01-01T12:08:04.00Z"))
        .tradeId("trade123")
        .orderId("order456")
        .fee(new BigDecimal("10.00"))
        .makerTaker(ThalexMakerTaker.MAKER)
        .rawJson("{\"example\":true}")
        .build();

    UserTrade userTrade = ThalexAdapters.toUserTrade(tradeDto);

    assertNotNull(userTrade);
    assertEquals(OrderType.BID, userTrade.getType());
    assertEquals(tradeDto.getAmount(), userTrade.getOriginalAmount());
    assertEquals(tradeDto.getPrice(), userTrade.getPrice());
    assertEquals(tradeDto.getTradeId(), userTrade.getId());
    assertEquals(tradeDto.getOrderId(), userTrade.getOrderId());
    assertEquals(tradeDto.getFee(), userTrade.getFeeAmount());
    assertEquals(new Currency("USD"), userTrade.getFeeCurrency());
    assertEquals(tradeDto.getRawJson(), userTrade.getRawJson());
    assertEquals(ThalexAdapters.getInstrumentFromMap(instrumentName), userTrade.getInstrument());
    assertEquals(Date.from(tradeDto.getTime()), userTrade.getTimestamp());
  }

  @Test
  public void testToFundingRecord_fromDepositTransaction() {
    ThalexTransactionDto dto = ThalexTransactionDto.builder()
        .asset(new Currency("BTC"))
        .amount(new BigDecimal("0.01"))
        .time(Instant.parse("2025-02-15T10:15:30.00Z"))
        .description("chain=testnet tx=xx_fake_8d5a2ef7683b7dc5d1c0d46a52ac3f40")
        .transactionType(ThalexTransactionType.DEPOSIT)
        .balanceAfter(new BigDecimal("1.01"))
        .rawJson("{\"tx\":123}")
        .build();

    FundingRecord record = ThalexAdapters.toFundingRecord(dto);

    assertNotNull(record);
    assertEquals(dto.getAsset(), record.getCurrency());
    assertEquals(dto.getAmount(), record.getAmount());
    assertEquals(Date.from(dto.getTime()), record.getDate());
    assertEquals(dto.getDescription(), record.getDescription());
    assertEquals("xx_fake_8d5a2ef7683b7dc5d1c0d46a52ac3f40", record.getBlockchainTransactionHash());
    assertEquals(dto.getBalanceAfter(), record.getBalance());
    assertEquals(Type.DEPOSIT, record.getType());
    assertEquals(dto.getRawJson(), record.getRawJson());
    assertNull(record.getAddress());
  }


  @Test
  public void testToFundingRecord_fromWithdrawalTransaction() {
    ThalexTransactionDto dto = ThalexTransactionDto.builder()
        .asset(new Currency("BTC"))
        .amount(new BigDecimal("0.01"))
        .time(Instant.parse("2025-02-15T10:15:30.00Z"))
        .description("withdrawal 0xdac17f958d2ee523a2206206994597c13d831ec7 <no_label>")
        .transactionType(ThalexTransactionType.WITHDRAWAL)
        .balanceAfter(new BigDecimal("1.01"))
        .rawJson("{\"tx\":123}")
        .build();

    FundingRecord record = ThalexAdapters.toFundingRecord(dto);

    assertNotNull(record);
    assertEquals(dto.getAsset(), record.getCurrency());
    assertEquals(dto.getAmount(), record.getAmount());
    assertEquals(Date.from(dto.getTime()), record.getDate());
    assertEquals(dto.getDescription(), record.getDescription());
    assertEquals("0xdac17f958d2ee523a2206206994597c13d831ec7", record.getAddress());
    assertEquals(dto.getBalanceAfter(), record.getBalance());
    assertEquals(Type.WITHDRAWAL, record.getType());
    assertEquals(dto.getRawJson(), record.getRawJson());
    assertNull(record.getBlockchainTransactionHash());
  }

  @Test
  public void testToFundingRecord_fromDeposit() {
    ThalexDeposit deposit = ThalexDeposit.builder()
        .transactionTimestamp(Instant.parse("2025-03-10T09:00:00Z"))
        .currency(new Currency("ETH"))
        .amount(new BigDecimal("2.5"))
        .transactionHash("0xabc123")
        .status(ThalexDepositStatus.CONFIRMED)
        .build();

    FundingRecord record = ThalexAdapters.toFundingRecord(deposit);

    assertNotNull(record);
    assertEquals(Date.from(deposit.getTransactionTimestamp()), record.getDate());
    assertEquals(deposit.getCurrency(), record.getCurrency());
    assertEquals(deposit.getAmount(), record.getAmount());
    assertEquals(deposit.getTransactionHash(), record.getBlockchainTransactionHash());
    assertEquals(FundingRecord.Type.DEPOSIT, record.getType());
    assertEquals(FundingRecord.Status.COMPLETE, record.getStatus());
  }

  @Test
  public void testToFundingRecord_fromWithdrawal() {
    ThalexWithdrawal withdrawal = ThalexWithdrawal.builder()
        .targetAddress("0xdef456")
        .createTime(Instant.parse("2025-04-01T15:30:00Z"))
        .currency(new Currency("USDT"))
        .amount(new BigDecimal("100.00"))
        .transactionHash("0xwithdraw123")
        .state(ThalexWithdrawalState.EXECUTED)
        .fee(new BigDecimal("1.00"))
        .remark("User initiated withdrawal")
        .build();

    FundingRecord record = ThalexAdapters.toFundingRecord(withdrawal);

    assertNotNull(record);
    assertEquals(withdrawal.getTargetAddress(), record.getAddress());
    assertEquals(Date.from(withdrawal.getCreateTime()), record.getDate());
    assertEquals(withdrawal.getCurrency(), record.getCurrency());
    assertEquals(withdrawal.getAmount(), record.getAmount());
    assertEquals(withdrawal.getTransactionHash(), record.getBlockchainTransactionHash());
    assertEquals(Type.WITHDRAWAL, record.getType());
    assertEquals(Status.COMPLETE, record.getStatus());
    assertEquals(withdrawal.getFee(), record.getFee());
    assertEquals(withdrawal.getRemark(), record.getDescription());
  }

  @Test
  public void testResolveStatus_fromThalexDepositStatus() {
    assertEquals(FundingRecord.Status.PROCESSING,
        Status.resolveStatus(ThalexDepositStatus.UNCONFIRMED.name()));
    assertEquals(FundingRecord.Status.COMPLETE,
        Status.resolveStatus(ThalexDepositStatus.CONFIRMED.name()));
  }

  @Test
  public void testResolveStatus_fromThalexWithdrawalState() {
    assertEquals(FundingRecord.Status.PROCESSING,
        Status.resolveStatus(ThalexWithdrawalState.PENDING.name()));
    assertEquals(FundingRecord.Status.PROCESSING,
        Status.resolveStatus(ThalexWithdrawalState.AWAITING_CONFIRMATION.name()));
    assertEquals(FundingRecord.Status.PROCESSING,
        Status.resolveStatus(ThalexWithdrawalState.EXECUTING.name()));
    assertEquals(FundingRecord.Status.COMPLETE,
        Status.resolveStatus(ThalexWithdrawalState.EXECUTED.name()));
    assertEquals(FundingRecord.Status.FAILED,
        Status.resolveStatus(ThalexWithdrawalState.REJECTED.name()));
  }

  @Test
  public void testGetOrderType_buy() {
    ThalexTradeDto trade = ThalexTradeDto.builder()
        .direction(ThalexDirection.BUY)
        .build();

    assertEquals(OrderType.BID, ThalexAdapters.getOrderType(trade));
  }

  @Test
  public void testGetOrderType_sell() {
    ThalexTradeDto trade = ThalexTradeDto.builder()
        .direction(ThalexDirection.SELL)
        .build();

    assertEquals(OrderType.ASK, ThalexAdapters.getOrderType(trade));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetOrderType_nullDirection() {
    ThalexTradeDto trade = ThalexTradeDto.builder().build();
    ThalexAdapters.getOrderType(trade);
  }

  @Test
  public void testGetMarketParticipant_maker() {
    ThalexTradeDto trade = ThalexTradeDto.builder()
        .makerTaker(ThalexMakerTaker.MAKER)
        .build();

    assertEquals(MarketParticipant.MAKER, ThalexAdapters.getMarketParticipant(trade));
  }

  @Test
  public void testGetMarketParticipant_taker() {
    ThalexTradeDto trade = ThalexTradeDto.builder()
        .makerTaker(ThalexMakerTaker.TAKER)
        .build();

    assertEquals(MarketParticipant.TAKER, ThalexAdapters.getMarketParticipant(trade));
  }

  @Test
  public void testGetMarketParticipant_nullMakerTaker_sellDirection() {
    ThalexTradeDto trade = ThalexTradeDto.builder()
        .direction(ThalexDirection.SELL)
        .build();

    assertNull(ThalexAdapters.getMarketParticipant(trade));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetMarketParticipant_nullMakerTaker_buyDirection() {
    ThalexTradeDto trade = ThalexTradeDto.builder()
        .direction(ThalexDirection.BUY)
        .build();

    ThalexAdapters.getMarketParticipant(trade);
  }

  @Test
  public void testToOpenPosition_longPosition() {
    ThalexPortfolioDto portfolio = ThalexPortfolioDto.builder()
        .position(new BigDecimal("1.5"))
        .averagePrice(new BigDecimal("28000"))
        .unrealisedPnl(new BigDecimal("150"))
        .build();

    OpenPosition position = ThalexAdapters.toOpenPosition(portfolio);

    assertNotNull(position);
    assertEquals(portfolio.getAveragePrice(), position.getPrice());
    assertEquals(portfolio.getPosition(), position.getSize());
    assertEquals(OpenPosition.Type.LONG, position.getType());
    assertEquals(portfolio.getUnrealisedPnl(), position.getUnRealisedPnl());
  }

  @Test
  public void testToOpenPosition_shortPosition() {
    ThalexPortfolioDto portfolio = ThalexPortfolioDto.builder()
        .position(new BigDecimal("-0.75"))
        .averagePrice(new BigDecimal("29500"))
        .unrealisedPnl(new BigDecimal("-80"))
        .build();

    OpenPosition position = ThalexAdapters.toOpenPosition(portfolio);

    assertNotNull(position);
    assertEquals(portfolio.getAveragePrice(), position.getPrice());
    assertEquals(portfolio.getPosition(), position.getSize());
    assertEquals(OpenPosition.Type.SHORT, position.getType());
    assertEquals(portfolio.getUnrealisedPnl(), position.getUnRealisedPnl());
  }

  @Test
  public void testGetInstrumentMetaData() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .minOrderAmount(new BigDecimal("0.001"))
        .tickSize(new BigDecimal("0.1"))
        .volumeTickSize(new BigDecimal("0.01"))
        .build();

    InstrumentMetaData metaData = ThalexAdapters.getInstrumentMetaData(dto);

    assertNotNull(metaData);
    assertEquals(dto.getMinOrderAmount(), metaData.getMinimumAmount());
    assertEquals(dto.getTickSize(), metaData.getPriceStepSize());
    assertEquals(dto.getVolumeTickSize(), metaData.getAmountStepSize());
    assertTrue(metaData.isMarketOrderEnabled());
  }

  @Test
  public void testToOptionsContract() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .instrumentName("BTC-13JUN25-115000-C")
        .expirationTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
        .strikePrice(new BigDecimal("30000"))
        .optionType(OptionType.CALL)
        .build();

    OptionsContract contract = ThalexAdapters.toOptionsContract(dto);

    assertNotNull(contract);
    assertEquals(new CurrencyPair("BTC", "USD"), contract.getCurrencyPair());
    assertEquals(Date.from(dto.getExpirationTimestamp()), contract.getExpireDate());
    assertEquals(dto.getStrikePrice(), contract.getStrike());
    assertEquals(dto.getOptionType(), contract.getType());
  }

  @Test
  public void testToFuturesTypeContract_perpetual() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .instrumentName("BTC-PERPETUAL")
        .build();

    FuturesContract contract = ThalexAdapters.toFuturesTypeContract(dto);

    assertNotNull(contract);
    assertEquals(new CurrencyPair("BTC", "USD"), contract.getCurrencyPair());
    assertEquals("PERPETUAL", contract.getPrompt());
    assertTrue(contract.isPerpetual());
  }

  @Test
  public void testToFuturesTypeContract_dated() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .instrumentName("ETH-20250628")
        .build();

    FuturesContract contract = ThalexAdapters.toFuturesTypeContract(dto);

    assertNotNull(contract);
    assertEquals(new CurrencyPair("ETH", "USD"), contract.getCurrencyPair());
    assertEquals("20250628", contract.getPrompt());
    assertFalse(contract.isPerpetual());
  }

  @Test
  public void testToCombinationsContract() {
    ThalexInstrumentLegDto legDto1 = ThalexInstrumentLegDto.builder()
        .instrumentName("ETH-26MAY25")
        .quantity(1)
        .build();

    ThalexInstrumentLegDto legDto2 = ThalexInstrumentLegDto.builder()
        .instrumentName("ETH-PERPETUAL")
        .quantity(-1)
        .build();

    List<ThalexInstrumentLegDto> legs = new ArrayList<>();
    legs.add(legDto1);
    legs.add(legDto2);

    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .instrumentName("ETH-26MAY25-PERPETUAL")
        .expirationTimestamp(Instant.parse("2025-05-26T10:00:00Z"))
        .legs(legs)
        .build();

    CombinationsContract contract = ThalexAdapters.toCombinationsContract(dto);

    assertNotNull(contract);
    assertEquals(new CurrencyPair("ETH", "USD"), contract.getCurrencyPair());
    assertEquals(dto.getInstrumentName(), contract.getInstrumentName());
    assertEquals(Date.from(dto.getExpirationTimestamp()), contract.getExpireDate());
    assertEquals(2, contract.getLegs().size());
    assertEquals(dto.getLegs().get(0).getInstrumentName(),
        contract.getLegs().get(0).getInstrumentName());
    assertEquals(dto.getLegs().get(0).getQuantity(),
        contract.getLegs().get(0).getQuantity());
    assertEquals(dto.getLegs().get(1).getInstrumentName(),
        contract.getLegs().get(1).getInstrumentName());
    assertEquals(dto.getLegs().get(1).getQuantity(),
        contract.getLegs().get(1).getQuantity());
  }

  @Test
  public void testToLeg() {
    ThalexInstrumentLegDto legDto = ThalexInstrumentLegDto.builder()
        .instrumentName("ETH-26MAY25")
        .quantity(1)
        .build();

    Leg leg = ThalexAdapters.toLeg(legDto);

    assertNotNull(leg);
    assertEquals(legDto.getInstrumentName(), leg.getInstrumentName());
    assertEquals(legDto.getQuantity(), leg.getQuantity());
  }

  @Test
  public void testToInstrumentsMapEntry_option() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .type(ThalexInstrumentType.OPTION)
        .instrumentName("ETH-26SEP25-1500-C")
        .expirationTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
        .strikePrice(new BigDecimal("30000"))
        .optionType(OptionType.CALL)
        .tickSize(new BigDecimal("0.1"))
        .volumeTickSize(new BigDecimal("0.01"))
        .minOrderAmount(new BigDecimal("0.001"))
        .build();

    Map.Entry<Instrument, InstrumentMetaData> entry = ThalexAdapters.toInstrumentsMapEntry(dto);

    assertNotNull(entry);
    assertTrue(entry.getKey() instanceof OptionsContract);
    assertNotNull(entry.getValue());
  }

  @Test
  public void testToInstrumentsMapEntry_future() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .type(ThalexInstrumentType.FUTURE)
        .instrumentName("ETH-26MAY25")
        .tickSize(new BigDecimal("0.1"))
        .volumeTickSize(new BigDecimal("0.01"))
        .minOrderAmount(new BigDecimal("0.001"))
        .build();

    Map.Entry<Instrument, InstrumentMetaData> entry = ThalexAdapters.toInstrumentsMapEntry(dto);

    assertNotNull(entry);
    assertTrue(entry.getKey() instanceof FuturesContract);
    assertNotNull(entry.getValue());
  }

  @Test
  public void testToInstrumentsMapEntry_perpetual() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .type(ThalexInstrumentType.PERPETUAL)
        .instrumentName("BTC-PERPETUAL")
        .tickSize(new BigDecimal("0.1"))
        .volumeTickSize(new BigDecimal("0.01"))
        .minOrderAmount(new BigDecimal("0.001"))
        .build();

    Map.Entry<Instrument, InstrumentMetaData> entry = ThalexAdapters.toInstrumentsMapEntry(dto);

    assertNotNull(entry);
    assertTrue(entry.getKey() instanceof FuturesContract);
    assertNotNull(entry.getValue());
  }

  @Test
  public void testToInstrumentsMapEntry_combination() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .type(ThalexInstrumentType.COMBINATION)
        .instrumentName("ETH-26MAY25-PERPETUAL")
        .expirationTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
        .legs(Collections.singletonList(
            ThalexInstrumentLegDto.builder()
                .instrumentName("ETH-26MAY25")
                .quantity(1)
                .build()
        ))
        .tickSize(new BigDecimal("0.1"))
        .volumeTickSize(new BigDecimal("0.01"))
        .minOrderAmount(new BigDecimal("0.001"))
        .build();

    Map.Entry<Instrument, InstrumentMetaData> entry = ThalexAdapters.toInstrumentsMapEntry(dto);

    assertNotNull(entry);
    assertTrue(entry.getKey() instanceof CombinationsContract);
    assertNotNull(entry.getValue());
  }

  @Test
  public void testToInstrumentsMapEntry_nullType() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .instrumentName("BTC-PERPETUAL")
        .build();

    assertNull(ThalexAdapters.toInstrumentsMapEntry(dto));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToInstrumentsMapEntry_unknownType() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .type(ThalexInstrumentType.valueOf("UNKNOWN"))
        .instrumentName("BTC-UNKNOWN")
        .build();

    ThalexAdapters.toInstrumentsMapEntry(dto);
  }

  @Test
  public void testToInstrument_option() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .type(ThalexInstrumentType.OPTION)
        .instrumentName("ETH-26SEP25-1500-C")
        .expirationTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
        .strikePrice(new BigDecimal("30000"))
        .optionType(OptionType.CALL)
        .build();

    Instrument instrument = ThalexAdapters.toInstrument(dto);

    assertNotNull(instrument);
    assertTrue(instrument instanceof OptionsContract);
  }

  @Test
  public void testToInstrument_future() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .type(ThalexInstrumentType.FUTURE)
        .instrumentName("ETH-26MAY25")
        .build();

    Instrument instrument = ThalexAdapters.toInstrument(dto);

    assertNotNull(instrument);
    assertTrue(instrument instanceof FuturesContract);
  }

  @Test
  public void testToInstrument_perpetual() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .type(ThalexInstrumentType.PERPETUAL)
        .instrumentName("BTC-PERPETUAL")
        .build();

    Instrument instrument = ThalexAdapters.toInstrument(dto);

    assertNotNull(instrument);
    assertTrue(instrument instanceof FuturesContract);
  }

  @Test
  public void testToInstrument_combination() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .type(ThalexInstrumentType.COMBINATION)
        .instrumentName("ETH-26MAY25-PERPETUAL")
        .expirationTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
        .legs(Collections.singletonList(
            ThalexInstrumentLegDto.builder()
                .instrumentName("ETH-26MAY25")
                .quantity(1)
                .build()
        ))
        .build();

    Instrument instrument = ThalexAdapters.toInstrument(dto);

    assertNotNull(instrument);
    assertTrue(instrument instanceof CombinationsContract);
  }

  @Test
  public void testToInstrument_nullType() {
    ThalexInstrumentDto dto = ThalexInstrumentDto.builder()
        .instrumentName("BTC-PERPETUAL")
        .build();

    assertNull(ThalexAdapters.toInstrument(dto));
  }

  @Test
  public void testToFoundingRecordType_allMappings() {
    assertEquals(Type.DEPOSIT,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.DEPOSIT));

    assertEquals(Type.WITHDRAWAL,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.WITHDRAWAL));

    assertEquals(Type.FEE,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.WITHDRAWAL_FEE));

    assertEquals(Type.SETTLEMENT,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.SESSION_SETTLEMENT));

    assertEquals(Type.INTEREST,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.PERPETUAL_FUNDING));

    assertEquals(Type.INTEREST,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.DAILY_INTEREST));

    assertEquals(Type.INTERNAL_WALLET_TRANSFER,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.INTERNAL_TRANSFER));

    assertEquals(Type.TRADE,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.ASSET_SWAP));

    assertEquals(Type.OTHER_INFLOW,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.REFERRAL_PROGRAM_PAYMENT));

    assertEquals(Type.OTHER_INFLOW,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.MARKET_VELOCITY_PROGRAM_PAYMENT));

    assertEquals(Type.OTHER_INFLOW,
        ThalexAdapters.toFoundingRecordType(ThalexTransactionType.MARKET_QUALITY_PROGRAM_PAYMENT));
  }

}