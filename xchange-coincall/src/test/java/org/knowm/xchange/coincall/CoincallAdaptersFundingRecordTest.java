package org.knowm.xchange.coincall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.knowm.xchange.coincall.dtos.account.CoincallSubaccountTransferRecordDto;
import org.knowm.xchange.coincall.dtos.account.CoincallSystemTransferDto;
import org.knowm.xchange.coincall.dtos.account.CoincallTransactionDto;
import org.knowm.xchange.coincall.dtos.enums.CoincallSystemTransferSide;
import org.knowm.xchange.coincall.dtos.enums.CoincallSystemTransferState;
import org.knowm.xchange.coincall.dtos.enums.CoincallSystemTransferType;
import org.knowm.xchange.coincall.dtos.enums.CoincallTransactionSide;
import org.knowm.xchange.coincall.dtos.enums.CoincallTransactionStatus;
import org.knowm.xchange.coincall.dtos.enums.CoincallUserType;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.instrument.Instrument;
import org.mockito.Mockito;

public class CoincallAdaptersFundingRecordTest {

  @Test
  public void testSubaccountTransferToFundingRecord_basicMapping_withSubaccounts() {
    CoincallSubaccountTransferRecordDto dto = new CoincallSubaccountTransferRecordDto();
    dto.setToken("BTC");
    dto.setCreateTime(1700000000000L);
    dto.setId(123L);
    dto.setAmount(new BigDecimal("0.5"));
    dto.setSourceUserType(CoincallUserType.SUBACCOUNT);
    dto.setSourceUserId(10L);
    dto.setToUserType(CoincallUserType.SUBACCOUNT);
    dto.setToUserId(20L);

    FundingRecord fr = CoincallAdapters.subaccountTransferToFundingRecord(dto);

    assertNotNull(fr);
    assertEquals(new Date(1700000000000L), fr.getDate());
    assertEquals(Currency.getInstance("BTC"), fr.getCurrency());
    assertEquals(new BigDecimal("0.5"), fr.getAmount());
    assertEquals("123", fr.getInternalId());
    assertEquals(FundingRecord.Type.INTERNAL_SUB_ACCOUNT_TRANSFER, fr.getType());
    assertEquals(FundingRecord.Status.COMPLETE, fr.getStatus());
    assertEquals("10", fr.getFromSubAccount());
    assertEquals("20", fr.getToSubAccount());
    assertNotNull(fr.getDescription()); // built by helper
  }

  @Test
  public void testSubaccountTransferToFundingRecord_nullCreateTimeAndId() {
    CoincallSubaccountTransferRecordDto dto = new CoincallSubaccountTransferRecordDto();
    dto.setToken("USDT");
    dto.setCreateTime(null);
    dto.setId(null);
    dto.setAmount(new BigDecimal("100"));
    dto.setSourceUserType(CoincallUserType.SUBACCOUNT);
    dto.setSourceUserId(1L);
    dto.setToUserType(CoincallUserType.SUBACCOUNT);
    dto.setToUserId(2L);

    FundingRecord fr = CoincallAdapters.subaccountTransferToFundingRecord(dto);

    assertNotNull(fr);
    assertNull(fr.getDate());
    assertNull(fr.getInternalId());
    assertEquals(Currency.getInstance("USDT"), fr.getCurrency());
    assertEquals(new BigDecimal("100"), fr.getAmount());
    assertEquals("1", fr.getFromSubAccount());
    assertEquals("2", fr.getToSubAccount());
  }

  @Test
  public void testSubaccountTransferToFundingRecord_nonSubaccountTypesIgnored() {
    CoincallSubaccountTransferRecordDto dto = new CoincallSubaccountTransferRecordDto();
    dto.setToken("ETH");
    dto.setCreateTime(1700001000000L);
    dto.setId(999L);
    dto.setAmount(new BigDecimal("2.0"));

    // user types not SUBACCOUNT (or null) → from/to subaccount should not be set
    dto.setSourceUserType(null);
    dto.setSourceUserId(10L);
    dto.setToUserType(null);
    dto.setToUserId(20L);

    FundingRecord fr = CoincallAdapters.subaccountTransferToFundingRecord(dto);

    assertNotNull(fr);
    assertEquals(new Date(1700001000000L), fr.getDate());
    assertEquals(Currency.getInstance("ETH"), fr.getCurrency());
    assertEquals(new BigDecimal("2.0"), fr.getAmount());
    assertEquals("999", fr.getInternalId());
    assertNull(fr.getFromSubAccount());
    assertNull(fr.getToSubAccount());
  }

  @Test
  public void testSubaccountTransfersToFundingRecords_nullInputReturnsEmptyList() {
    List<FundingRecord> result = CoincallAdapters.subaccountTransfersToFundingRecords(null);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testSubaccountTransfersToFundingRecords_filtersNullsAndMaps() {
    CoincallSubaccountTransferRecordDto dto1 = new CoincallSubaccountTransferRecordDto();
    dto1.setToken("BTC");
    dto1.setCreateTime(1700000000000L);
    dto1.setId(1L);
    dto1.setAmount(new BigDecimal("0.1"));
    dto1.setSourceUserType(CoincallUserType.SUBACCOUNT);
    dto1.setSourceUserId(11L);
    dto1.setToUserType(CoincallUserType.SUBACCOUNT);
    dto1.setToUserId(21L);

    CoincallSubaccountTransferRecordDto dto2 = new CoincallSubaccountTransferRecordDto();
    dto2.setToken("USDT");
    dto2.setCreateTime(1700001000000L);
    dto2.setId(2L);
    dto2.setAmount(new BigDecimal("50"));
    dto2.setSourceUserType(CoincallUserType.SUBACCOUNT);
    dto2.setSourceUserId(12L);
    dto2.setToUserType(CoincallUserType.SUBACCOUNT);
    dto2.setToUserId(22L);

    List<CoincallSubaccountTransferRecordDto> input =
        Arrays.asList(dto1, null, dto2);

    List<FundingRecord> result = CoincallAdapters.subaccountTransfersToFundingRecords(input);

    assertEquals(2, result.size());

    FundingRecord fr1 = result.get(0);
    FundingRecord fr2 = result.get(1);

    assertEquals("1", fr1.getInternalId());
    assertEquals(Currency.getInstance("BTC"), fr1.getCurrency());
    assertEquals("11", fr1.getFromSubAccount());
    assertEquals("21", fr1.getToSubAccount());

    assertEquals("2", fr2.getInternalId());
    assertEquals(Currency.getInstance("USDT"), fr2.getCurrency());
    assertEquals("12", fr2.getFromSubAccount());
    assertEquals("22", fr2.getToSubAccount());
  }

  @Test
  public void testToFundingRecord_basicMapping_fromTransaction() {
    Instant createTime = Instant.ofEpochMilli(1700000000000L);

    CoincallTransactionDto dto = new CoincallTransactionDto();
    dto.setCoin("BTC");
    dto.setSide(CoincallTransactionSide.DEPOSIT);
    dto.setStatus(CoincallTransactionStatus.COMPLETED);
    dto.setTransactionRecordId(12345L);
    dto.setCreateTime(createTime);
    dto.setAddress("addr-1");
    dto.setAmount(new BigDecimal("0.5"));
    dto.setTxId("tx-hash-1");
    dto.setServiceFee(new BigDecimal("0.0001"));

    FundingRecord fr = CoincallAdapters.toFundingRecord(dto);

    assertNotNull(fr);
    assertEquals("addr-1", fr.getAddress());
    assertEquals(Date.from(createTime), fr.getDate());
    assertEquals(Currency.getInstance("BTC"), fr.getCurrency());
    assertEquals(new BigDecimal("0.5"), fr.getAmount());
    assertEquals("12345", fr.getInternalId());
    assertEquals("tx-hash-1", fr.getBlockchainTransactionHash());
    assertEquals(new BigDecimal("0.0001"), fr.getFee());

    assertNotNull(fr.getType());
    assertNotNull(fr.getStatus());
    assertNotNull(fr.getDescription());
  }

  @Test
  public void testToFundingRecord_nullTransactionRecordId() {
    CoincallTransactionDto dto = new CoincallTransactionDto();
    dto.setCoin("USDT");
    dto.setStatus(CoincallTransactionStatus.COMPLETED);
    dto.setSide(CoincallTransactionSide.WITHDRAW);
    dto.setTransactionRecordId(null);
    dto.setCreateTime(Instant.ofEpochSecond(1_700_000_000L));
    dto.setAmount(new BigDecimal("100"));
    dto.setTxId("tx-hash-2");
    dto.setServiceFee(new BigDecimal("1"));

    FundingRecord fr = CoincallAdapters.toFundingRecord(dto);

    assertNotNull(fr);
    assertNull(fr.getInternalId());
    assertEquals(Currency.getInstance("USDT"), fr.getCurrency());
    assertEquals(new BigDecimal("100"), fr.getAmount());
    assertEquals("tx-hash-2", fr.getBlockchainTransactionHash());
  }

  @Test
  public void testToFundingRecord_nullCreateTime() {
    CoincallTransactionDto dto = new CoincallTransactionDto();
    dto.setCoin("ETH");
    dto.setStatus(CoincallTransactionStatus.COMPLETED);
    dto.setSide(CoincallTransactionSide.DEPOSIT);
    dto.setCreateTime(null);
    dto.setTransactionRecordId(999L);
    dto.setAmount(new BigDecimal("2.0"));
    dto.setTxId("tx-hash-3");
    dto.setServiceFee(new BigDecimal("0.01"));

    FundingRecord fr = CoincallAdapters.toFundingRecord(dto);

    assertNotNull(fr);
    assertNull(fr.getDate());
    assertEquals(Currency.getInstance("ETH"), fr.getCurrency());
    assertEquals(new BigDecimal("2.0"), fr.getAmount());
    assertEquals("999", fr.getInternalId());
  }

  @Test
  public void testToFundingRecords_nullInputReturnsEmptyList() {
    List<FundingRecord> result = CoincallAdapters.toFundingRecords(null);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testToFundingRecords_filtersNullsAndMaps() {
    CoincallTransactionDto dto1 = new CoincallTransactionDto();
    dto1.setCoin("BTC");
    dto1.setStatus(CoincallTransactionStatus.COMPLETED);
    dto1.setSide(CoincallTransactionSide.DEPOSIT);
    dto1.setTransactionRecordId(1L);
    dto1.setCreateTime(Instant.ofEpochMilli(1700000000000L));
    dto1.setAmount(new BigDecimal("0.1"));
    dto1.setTxId("tx-1");

    CoincallTransactionDto dto2 = new CoincallTransactionDto();
    dto2.setCoin("USDT");
    dto2.setStatus(CoincallTransactionStatus.COMPLETED);
    dto2.setSide(CoincallTransactionSide.WITHDRAW);
    dto2.setTransactionRecordId(2L);
    dto2.setCreateTime(Instant.ofEpochMilli(1700001000000L));
    dto2.setAmount(new BigDecimal("50"));
    dto2.setTxId("tx-2");

    List<CoincallTransactionDto> input =
        Arrays.asList(dto1, null, dto2);

    List<FundingRecord> result = CoincallAdapters.toFundingRecords(input);

    assertEquals(2, result.size());
    assertEquals("1", result.get(0).getInternalId());
    assertEquals(Currency.getInstance("BTC"), result.get(0).getCurrency());
    assertEquals("2", result.get(1).getInternalId());
    assertEquals(Currency.getInstance("USDT"), result.get(1).getCurrency());
  }

  @Test
  public void testToFundingRecord_basicMapping_fromUserTrade() {
    Instrument instrument = Mockito.mock(Instrument.class);
    Mockito.when(instrument.getBase()).thenReturn(Currency.getInstance("BTC"));

    UserTrade trade = new UserTrade.Builder()
        .timestamp(new Date(1700000000000L))
        .originalAmount(new BigDecimal("0.25"))
        .feeAmount(new BigDecimal("0.001"))
        .id("trade123")
        .instrument(instrument)
        .orderUserReference("order-ref")
        .rawJson("{\"raw\":true}")
        .build();

    FundingRecord fr = CoincallAdapters.toFundingRecord(trade);

    assertNotNull(fr);
    assertEquals(new Date(1700000000000L), fr.getDate());
    assertEquals(Currency.getInstance("BTC"), fr.getCurrency());
    assertEquals(new BigDecimal("0.25"), fr.getAmount());
    assertEquals("trade123", fr.getInternalId());
    assertEquals(FundingRecord.Type.TRADE, fr.getType());
    assertEquals(FundingRecord.Status.COMPLETE, fr.getStatus());
    assertEquals(new BigDecimal("0.001"), fr.getFee());
    assertEquals("order-ref", fr.getDescription());
    assertEquals("{\"raw\":true}", fr.getRawJson());
  }

  @Test
  public void testToFundingRecord_negativeAmountAndFeeAreAbs() {
    Instrument instrument = Mockito.mock(Instrument.class);
    Mockito.when(instrument.getBase()).thenReturn(Currency.getInstance("ETH"));

    UserTrade trade = new UserTrade.Builder()
        .timestamp(new Date())
        .originalAmount(new BigDecimal("-5"))
        .feeAmount(new BigDecimal("-0.5"))
        .instrument(instrument)
        .build();

    FundingRecord fr = CoincallAdapters.toFundingRecord(trade);

    assertNotNull(fr);
    assertEquals(new BigDecimal("5"), fr.getAmount());
    assertEquals(new BigDecimal("0.5"), fr.getFee());
  }

  @Test
  public void testToFundingRecord_noFeeProvided() {
    Instrument instrument = Mockito.mock(Instrument.class);
    Mockito.when(instrument.getBase()).thenReturn(Currency.getInstance("SOL"));

    UserTrade trade = new UserTrade.Builder()
        .timestamp(new Date())
        .originalAmount(new BigDecimal("1.0"))
        .instrument(instrument)
        .build();

    FundingRecord fr = CoincallAdapters.toFundingRecord(trade);

    assertNotNull(fr);
    assertEquals(new BigDecimal("1.0"), fr.getAmount());
    assertNull(fr.getFee());
  }

  @Test
  public void testToFundingRecord_noOriginalAmount() {
    Instrument instrument = Mockito.mock(Instrument.class);
    Mockito.when(instrument.getBase()).thenReturn(Currency.getInstance("XRP"));

    UserTrade trade = new UserTrade.Builder()
        .timestamp(new Date())
        .feeAmount(new BigDecimal("0.01"))
        .instrument(instrument)
        .build();

    FundingRecord fr = CoincallAdapters.toFundingRecord(trade);

    assertNotNull(fr);
    assertNull(fr.getAmount());
    assertEquals(new BigDecimal("0.01"), fr.getFee());
  }

  @Test
  public void testToFundingRecord_nullInput() {
    assertNull(CoincallAdapters.toFundingRecord((CoincallTransactionDto) null));
  }

  @Test
  public void testToFundingRecord_basicMapping_fromSystemTransfer() {
    CoincallSystemTransferDto dto = new CoincallSystemTransferDto();
    dto.setTime(1700000000000L);
    dto.setCoin("BTC");
    dto.setCreditChange(new BigDecimal("123.45"));
    dto.setTxId("tx123");
    dto.setType(CoincallSystemTransferType.CREDIT);
    dto.setSide(CoincallSystemTransferSide.INCREASE);
    dto.setState(CoincallSystemTransferState.SETTLED);
    dto.setNote("test note");

    FundingRecord fr = CoincallAdapters.toFundingRecord(dto);

    assertNotNull(fr);
    assertEquals(new Date(1700000000000L), fr.getDate());
    assertEquals(Currency.getInstance("BTC"), fr.getCurrency());
    assertEquals(new BigDecimal("123.45"), fr.getAmount());
    assertEquals("tx123", fr.getInternalId());
    assertEquals(FundingRecord.Type.OTHER_INFLOW, fr.getType());   // from CREDIT + INCREASE
    assertEquals(FundingRecord.Status.COMPLETE, fr.getStatus());   // from SETTLED
    assertEquals("test note", fr.getDescription());
  }

  @Test
  public void testToFundingRecord_unknownCurrency() {
    CoincallSystemTransferDto dto = new CoincallSystemTransferDto();
    dto.setCoin("NOTACOIN");  // invalid for Currency.getInstance()
    dto.setTime(1000L);

    FundingRecord fr = CoincallAdapters.toFundingRecord(dto);

    assertNotNull(fr);
    assertEquals(new Currency("NOTACOIN"), fr.getCurrency());
  }

  @Test
  public void testToFundingRecord_nullTime() {
    CoincallSystemTransferDto dto = new CoincallSystemTransferDto();
    dto.setCoin("USDT");
    dto.setTime(null);

    FundingRecord fr = CoincallAdapters.toFundingRecord(dto);

    assertNotNull(fr);
    assertNull(fr.getDate());
  }

  @Test
  public void testMapFundingType_nullType_increaseSide() {
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(null, CoincallSystemTransferSide.INCREASE);

    assertEquals(FundingRecord.Type.OTHER_INFLOW, result);
  }

  @Test
  public void testMapFundingType_nullType_decreaseSide() {
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(null, CoincallSystemTransferSide.DECREASE);

    assertEquals(FundingRecord.Type.OTHER_OUTFLOW, result);
  }

  @Test
  public void testMapFundingType_credit_increase() {
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(
            CoincallSystemTransferType.CREDIT, CoincallSystemTransferSide.INCREASE);

    assertEquals(FundingRecord.Type.OTHER_INFLOW, result);
  }

  @Test
  public void testMapFundingType_credit_decrease() {
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(
            CoincallSystemTransferType.CREDIT, CoincallSystemTransferSide.DECREASE);

    assertEquals(FundingRecord.Type.OTHER_OUTFLOW, result);
  }

  @Test
  public void testMapFundingType_trialBonus_increase() {
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(
            CoincallSystemTransferType.TRIAL_BONUS, CoincallSystemTransferSide.INCREASE);

    assertEquals(FundingRecord.Type.OTHER_INFLOW, result);
  }

  @Test
  public void testMapFundingType_trialBonus_decrease() {
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(
            CoincallSystemTransferType.TRIAL_BONUS, CoincallSystemTransferSide.DECREASE);

    assertEquals(FundingRecord.Type.OTHER_OUTFLOW, result);
  }

  @Test
  public void testMapFundingType_rewards_increase() {
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(
            CoincallSystemTransferType.REWARDS, CoincallSystemTransferSide.INCREASE);

    assertEquals(FundingRecord.Type.AIRDROP, result);
  }

  @Test
  public void testMapFundingType_rewards_decrease() {
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(
            CoincallSystemTransferType.REWARDS, CoincallSystemTransferSide.DECREASE);

    assertEquals(FundingRecord.Type.OTHER_OUTFLOW, result);
  }

  @Test
  public void testMapFundingType_transfer_increase() {
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(
            CoincallSystemTransferType.TRANSFER, CoincallSystemTransferSide.INCREASE);

    assertEquals(FundingRecord.Type.INTERNAL_DEPOSIT, result);
  }

  @Test
  public void testMapFundingType_transfer_decrease() {
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(
            CoincallSystemTransferType.TRANSFER, CoincallSystemTransferSide.DECREASE);

    assertEquals(FundingRecord.Type.INTERNAL_WITHDRAWAL, result);
  }

  @Test
  public void testMapFundingType_releaseTrialBonus_anySide() {
    // side shouldn't matter
    FundingRecord.Type result =
        CoincallAdapters.mapFundingType(
            CoincallSystemTransferType.RELEASE_TRIAL_BONUS, CoincallSystemTransferSide.INCREASE);

    assertEquals(FundingRecord.Type.OTHER_OUTFLOW, result);
  }

  @Test
  public void testMapFundingStatus_unsettled() {
    FundingRecord.Status result =
        CoincallAdapters.mapFundingStatus(CoincallSystemTransferState.UNSETTLED);
    assertEquals(FundingRecord.Status.PROCESSING, result);
  }

  @Test
  public void testMapFundingStatus_settled() {
    FundingRecord.Status result =
        CoincallAdapters.mapFundingStatus(CoincallSystemTransferState.SETTLED);
    assertEquals(FundingRecord.Status.COMPLETE, result);
  }

}