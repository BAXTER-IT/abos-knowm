package org.knowm.xchange.kucoin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.kucoin.dto.response.OrderResponse;

public class KucoinAdaptersTest {
  
  @Test
  public void adaptOpenPosition_nullOrder_returnsNull() {
    OpenPosition position = KucoinAdapters.adaptOpenPosition(null);
    assertNull(position);
  }

  @Test
  public void adaptOpenPosition_buySide_mapsToLong() {
    OrderResponse order = new OrderResponse();
    order.setSymbol("BTC-USDT");
    order.setSide("buy");
    order.setSize(new BigDecimal("0.123"));
    order.setPrice(new BigDecimal("50000"));

    OpenPosition position = KucoinAdapters.adaptOpenPosition(order);

    assertNotNull(position);
    assertEquals(new CurrencyPair("BTC-USDT"), position.getInstrument());
    assertEquals(OpenPosition.Type.LONG, position.getType());
    assertEquals(new BigDecimal("0.123"), position.getSize());
    assertEquals(new BigDecimal("50000"), position.getPrice());
    assertNull(position.getLiquidationPrice());
    assertNull(position.getUnRealisedPnl());
  }

  @Test
  public void adaptOpenPosition_sellSide_mapsToShort() {
    OrderResponse order = new OrderResponse();
    order.setSymbol("ETH-USDT");
    order.setSide("SELL"); // test case-insensitivity
    order.setSize(new BigDecimal("1.5"));
    order.setPrice(new BigDecimal("2500"));

    OpenPosition position = KucoinAdapters.adaptOpenPosition(order);

    assertNotNull(position);
    assertEquals(new CurrencyPair("ETH-USDT"), position.getInstrument());
    assertEquals(OpenPosition.Type.SHORT, position.getType());
    assertEquals(new BigDecimal("1.5"), position.getSize());
    assertEquals(new BigDecimal("2500"), position.getPrice());
  }

  @Test
  public void adaptOpenPositions_emptyList_returnsEmptyList() {
    List<OpenPosition> result =
        KucoinAdapters.adaptOpenPositions(Collections.emptyList());

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void adaptOpenPositions_mapsAllElements() {
    OrderResponse order1 = new OrderResponse();
    order1.setSymbol("BTC-USDT");
    order1.setSide("buy");
    order1.setSize(new BigDecimal("0.01"));
    order1.setPrice(new BigDecimal("50000"));

    OrderResponse order2 = new OrderResponse();
    order2.setSymbol("ETH-USDT");
    order2.setSide("sell");
    order2.setSize(new BigDecimal("2"));
    order2.setPrice(new BigDecimal("2500"));

    List<OpenPosition> result =
        KucoinAdapters.adaptOpenPositions(Arrays.asList(order1, order2));

    assertNotNull(result);
    assertEquals(2, result.size());

    OpenPosition pos1 = result.get(0);
    OpenPosition pos2 = result.get(1);

    assertEquals(new CurrencyPair("BTC-USDT"), pos1.getInstrument());
    assertEquals(OpenPosition.Type.LONG, pos1.getType());
    assertEquals(new BigDecimal("0.01"), pos1.getSize());
    assertEquals(new BigDecimal("50000"), pos1.getPrice());

    assertEquals(new CurrencyPair("ETH-USDT"), pos2.getInstrument());
    assertEquals(OpenPosition.Type.SHORT, pos2.getType());
    assertEquals(new BigDecimal("2"), pos2.getSize());
    assertEquals(new BigDecimal("2500"), pos2.getPrice());
  }

  @Test
  public void adaptOpenPositions_filterNullElements() {
    OrderResponse order = new OrderResponse();
    order.setSymbol("BTC-USDT");
    order.setSide("buy");
    order.setSize(new BigDecimal("0.01"));
    order.setPrice(new BigDecimal("50000"));

    List<OpenPosition> result =
        KucoinAdapters.adaptOpenPositions(Arrays.asList(order, null));

    assertEquals(1, result.size());
    assertNotNull(result.get(0));
  }
  
}