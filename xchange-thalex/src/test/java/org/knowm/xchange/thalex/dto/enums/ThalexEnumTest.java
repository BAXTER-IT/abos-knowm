package org.knowm.xchange.thalex.dto.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ThalexEnumTest {

  @Test
  public void testThalexDepositStatus_fromThalexValue_valid() {
    assertEquals(ThalexDepositStatus.UNCONFIRMED,
        ThalexDepositStatus.fromThalexValue("unconfirmed"));
    assertEquals(ThalexDepositStatus.CONFIRMED, ThalexDepositStatus.fromThalexValue("confirmed"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testThalexDepositStatus_fromThalexValue_invalid() {
    ThalexDepositStatus.fromThalexValue("invalid");
  }

  @Test
  public void testThalexDirection_fromThalexValue_valid() {
    assertEquals(ThalexDirection.BUY, ThalexDirection.fromThalexValue("buy"));
    assertEquals(ThalexDirection.SELL, ThalexDirection.fromThalexValue("sell"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testThalexDirection_fromThalexValue_invalid() {
    ThalexDirection.fromThalexValue("hold");
  }

  @Test
  public void testThalexInstrumentType_fromThalexValue_valid() {
    assertEquals(ThalexInstrumentType.PERPETUAL, ThalexInstrumentType.fromThalexValue("perpetual"));
    assertEquals(ThalexInstrumentType.FUTURE, ThalexInstrumentType.fromThalexValue("future"));
    assertEquals(ThalexInstrumentType.OPTION, ThalexInstrumentType.fromThalexValue("option"));
    assertEquals(ThalexInstrumentType.COMBINATION,
        ThalexInstrumentType.fromThalexValue("combination"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testThalexInstrumentType_fromThalexValue_invalid() {
    ThalexInstrumentType.fromThalexValue("swap");
  }

  @Test
  public void testThalexMakerTaker_fromThalexValue_valid() {
    assertEquals(ThalexMakerTaker.MAKER, ThalexMakerTaker.fromThalexValue("maker"));
    assertEquals(ThalexMakerTaker.TAKER, ThalexMakerTaker.fromThalexValue("taker"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testThalexMakerTaker_fromThalexValue_invalid() {
    ThalexMakerTaker.fromThalexValue("none");
  }

  @Test
  public void testThalexTradeType_fromThalexValue_valid() {
    assertEquals(ThalexTradeType.NORMAL, ThalexTradeType.fromThalexValue("normal"));
    assertEquals(ThalexTradeType.BLOCK, ThalexTradeType.fromThalexValue("block"));
    assertEquals(ThalexTradeType.COMBO, ThalexTradeType.fromThalexValue("combo"));
    assertEquals(ThalexTradeType.AMEND, ThalexTradeType.fromThalexValue("amend"));
    assertEquals(ThalexTradeType.DELETE, ThalexTradeType.fromThalexValue("delete"));
    assertEquals(ThalexTradeType.INTERNAL_TRANSFER,
        ThalexTradeType.fromThalexValue("internal_transfer"));
    assertEquals(ThalexTradeType.EXPIRATION, ThalexTradeType.fromThalexValue("expiration"));
    assertEquals(ThalexTradeType.DAILY_MARK, ThalexTradeType.fromThalexValue("daily_mark"));
    assertEquals(ThalexTradeType.RFQ, ThalexTradeType.fromThalexValue("rfq"));
    assertEquals(ThalexTradeType.LIQUIDATION, ThalexTradeType.fromThalexValue("liquidation"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testThalexTradeType_fromThalexValue_invalid() {
    ThalexTradeType.fromThalexValue("unknown");
  }

  @Test
  public void testThalexWithdrawalState_fromThalexValue_valid() {
    assertEquals(ThalexWithdrawalState.PENDING, ThalexWithdrawalState.fromThalexValue("pending"));
    assertEquals(ThalexWithdrawalState.AWAITING_CONFIRMATION,
        ThalexWithdrawalState.fromThalexValue("awaiting_confirmation"));
    assertEquals(ThalexWithdrawalState.EXECUTING,
        ThalexWithdrawalState.fromThalexValue("executing"));
    assertEquals(ThalexWithdrawalState.EXECUTED, ThalexWithdrawalState.fromThalexValue("executed"));
    assertEquals(ThalexWithdrawalState.REJECTED, ThalexWithdrawalState.fromThalexValue("rejected"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testThalexWithdrawalState_fromThalexValue_invalid() {
    ThalexWithdrawalState.fromThalexValue("abandoned");
  }
}