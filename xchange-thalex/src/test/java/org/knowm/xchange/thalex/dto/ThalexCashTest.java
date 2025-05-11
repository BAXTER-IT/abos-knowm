package org.knowm.xchange.thalex.dto;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import org.junit.Test;
import org.knowm.xchange.currency.Currency;

public class ThalexCashTest {

  @Test
  public void testThalexCash_equalsAndToString() {
    ThalexCash cash1 = ThalexCash.builder()
        .currency(new Currency("BTC"))
        .balance(new BigDecimal("0.5"))
        .collateralFactor(new BigDecimal("0.8"))
        .collateralIndexPrice(new BigDecimal("30000"))
        .transactable(true)
        .build();

    ThalexCash cash2 = ThalexCash.builder()
        .currency(new Currency("BTC"))
        .balance(new BigDecimal("0.5"))
        .collateralFactor(new BigDecimal("0.8"))
        .collateralIndexPrice(new BigDecimal("30000"))
        .transactable(true)
        .build();

    assertEquals(cash1, cash2);
    assertEquals(cash1.hashCode(), cash2.hashCode());

    String expectedToString = "ThalexCash(currency=BTC, balance=0.5, collateralFactor=0.8, collateralIndexPrice=30000, transactable=true)";
    assertEquals(expectedToString, cash1.toString());
  }

}