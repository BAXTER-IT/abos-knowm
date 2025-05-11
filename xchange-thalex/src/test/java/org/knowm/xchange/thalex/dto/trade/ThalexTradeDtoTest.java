package org.knowm.xchange.thalex.dto.trade;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.Test;
import org.knowm.xchange.thalex.dto.enums.ThalexDirection;
import org.knowm.xchange.thalex.dto.enums.ThalexMakerTaker;
import org.knowm.xchange.thalex.dto.enums.ThalexTradeType;

public class ThalexTradeDtoTest {

  @Test
  public void testThalexTradeDto_equalsAndToString() {
    ThalexTradeDto trade1 = ThalexTradeDto.builder()
        .tradeType(ThalexTradeType.NORMAL)
        .tradeId("T123")
        .orderId("O456")
        .instrumentName("BTC-PERPETUAL")
        .direction(ThalexDirection.BUY)
        .price(new BigDecimal("30000"))
        .amount(new BigDecimal("0.1"))
        .label("scalping")
        .time(Instant.parse("2025-01-01T00:00:00Z"))
        .positionAfter(new BigDecimal("0.1"))
        .sessionRealisedAfter(new BigDecimal("50"))
        .positionPnl(new BigDecimal("20"))
        .perpetualFundingPnl(new BigDecimal("5"))
        .fee(new BigDecimal("2"))
        .index(new BigDecimal("29900"))
        .feeRate(new BigDecimal("0.001"))
        .fundingMark(new BigDecimal("0.0005"))
        .liquidationFee(new BigDecimal("0.1"))
        .clientOrderId("client-789")
        .makerTaker(ThalexMakerTaker.MAKER)
        .rawJson("{\"some\":\"json\"}")
        .build();

    ThalexTradeDto trade2 = ThalexTradeDto.builder()
        .tradeType(ThalexTradeType.NORMAL)
        .tradeId("T123")
        .orderId("O456")
        .instrumentName("BTC-PERPETUAL")
        .direction(ThalexDirection.BUY)
        .price(new BigDecimal("30000"))
        .amount(new BigDecimal("0.1"))
        .label("scalping")
        .time(Instant.parse("2025-01-01T00:00:00Z"))
        .positionAfter(new BigDecimal("0.1"))
        .sessionRealisedAfter(new BigDecimal("50"))
        .positionPnl(new BigDecimal("20"))
        .perpetualFundingPnl(new BigDecimal("5"))
        .fee(new BigDecimal("2"))
        .index(new BigDecimal("29900"))
        .feeRate(new BigDecimal("0.001"))
        .fundingMark(new BigDecimal("0.0005"))
        .liquidationFee(new BigDecimal("0.1"))
        .clientOrderId("client-789")
        .makerTaker(ThalexMakerTaker.MAKER)
        .rawJson("{\"some\":\"json\"}")
        .build();

    assertEquals(trade1, trade2);
    assertEquals(trade1.hashCode(), trade2.hashCode());

    String expectedToString = "ThalexTradeDto(tradeType=NORMAL, tradeId=T123, orderId=O456, instrumentName=BTC-PERPETUAL, direction=BUY, price=30000, amount=0.1, label=scalping, time=2025-01-01T00:00:00Z, positionAfter=0.1, sessionRealisedAfter=50, positionPnl=20, perpetualFundingPnl=5, fee=2, index=29900, feeRate=0.001, fundingMark=0.0005, liquidationFee=0.1, clientOrderId=client-789, makerTaker=MAKER, rawJson={\"some\":\"json\"})";
    assertEquals(expectedToString, trade1.toString());
  }

}