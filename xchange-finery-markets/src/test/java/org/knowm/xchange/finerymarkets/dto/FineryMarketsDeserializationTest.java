package org.knowm.xchange.finerymarkets.dto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsCurrency;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsInstrument;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsNetwork;
import org.knowm.xchange.finerymarkets.dto.marketdata.response.InstrumentsResponse;
import org.knowm.xchange.finerymarkets.dto.trade.DealHistory;
import org.knowm.xchange.finerymarkets.dto.trade.OrderCreatedBy;
import org.knowm.xchange.finerymarkets.dto.trade.response.DealHistoryResponse;

public class FineryMarketsDeserializationTest {

  ObjectMapper mapper;

  @Before
  public void setUp() {
    mapper = new ObjectMapper();
  }

  @Test
  public void testInstrumentsResponse() throws IOException {
    String response =
        IOUtils.resourceToString("/instruments_20240216.json", StandardCharsets.UTF_8);

    InstrumentsResponse instrumentsResponse = mapper.readValue(response, InstrumentsResponse.class);

    FineryMarketsCurrency firstCurrency = instrumentsResponse.getCurrencies().get(0);
    FineryMarketsInstrument firstInstrument = instrumentsResponse.getInstruments().get(0);
    FineryMarketsNetwork secondNetwork = instrumentsResponse.getNetworks().get(1);

    assertEquals(14, instrumentsResponse.getCurrencies().size());
    assertEquals("OP", firstCurrency.getName());
    assertEquals(121, firstCurrency.getId());
    assertEquals(1, firstCurrency.getBalanceStep());
    assertEquals(341000000, firstCurrency.getPrice());
    assertEquals("crypto", firstCurrency.getTypeName());
    assertArrayEquals(
        new String[] {"Arbitrum", "AVAX", "SOL", "BNB"}, firstCurrency.getNetworks().toArray());
    assertEquals(
        "[\"OP\",121,1,341000000,\"crypto\",[\"Arbitrum\",\"AVAX\",\"SOL\",\"BNB\"]]",
        firstCurrency.getRawData());

    assertEquals(3, instrumentsResponse.getInstruments().size());
    assertEquals("USDC-INR", firstInstrument.getName());
    assertEquals(563, firstInstrument.getId());
    assertEquals("USDC", firstInstrument.getAssetCurrencyName());
    assertEquals("INR", firstInstrument.getBalanceCurrencyName());
    assertEquals("[\"USDC-INR\",563,\"USDC\",\"INR\"]", firstInstrument.getRawData());

    assertEquals(3, instrumentsResponse.getNetworks().size());
    assertEquals("XTZ", secondNetwork.getName());
    assertEquals("Tezos", secondNetwork.getDescription());
    assertEquals(28, secondNetwork.getId());
    assertEquals("[\"XTZ\",\"Tezos\",28]", secondNetwork.getRawData());
  }

  @Test
  public void testInstrumentsResponse_when_no_networks() throws IOException {
    String response =
        IOUtils.resourceToString("/instruments_20240216_no_networks.json", StandardCharsets.UTF_8);

    InstrumentsResponse instrumentsResponse = mapper.readValue(response, InstrumentsResponse.class);

    assertNotNull(instrumentsResponse.getNetworks());
    assertTrue(instrumentsResponse.getNetworks().isEmpty());
  }

  @Test
  public void testDealHistoryResponse() throws IOException {
    String response = IOUtils.resourceToString("/dealHistory_test_01.json", StandardCharsets.UTF_8);

    DealHistoryResponse dealHistoryResponse = mapper.readValue(response, DealHistoryResponse.class);

    assertEquals(2, dealHistoryResponse.getDeals().size());
    String expectedRawJson =
        "[\"BTC-USD\",0,0,0,1234,0,9900000000,10000000,9998000,1558051200000,1558052600000,12,1,9900000000,2000,19800000000000,100000,0,1,1234,4321]";
    DealHistory expected =
        DealHistory.builder()
            .instrumentName("BTC-USD")
            .orderType(OrderType.LIMIT)
            .side(Side.BID)
            .cancelReason(CancelReason.IN_PLACE_OR_FILLED)
            .orderId(1234)
            .clientOrderId(0)
            .orderPrice(9900000000L)
            .orderInitialSize(10000000)
            .remainingOrderSize(9998000)
            .orderCreatedAt(1558051200000L)
            .dealMoment(1558052600000L)
            .dealId(12)
            .dealAggressorSide(Side.ASK)
            .dealPrice(9900000000L)
            .dealSize(2000)
            .dealVolume(19800000000000L)
            .dealDelta(100000)
            .counterpartyId(0)
            .bySizeOrVolume(OrderCreatedBy.VOLUME)
            .counterpartySubaccountId(1234)
            .linkedDealId(4321)
            .rawData(expectedRawJson)
            .build();

    DealHistory actual = dealHistoryResponse.getDeals().get(0);
    assertEquals(expected, actual);
  }
}
