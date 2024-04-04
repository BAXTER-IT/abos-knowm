package org.knowm.xchange.finerymarkets.dto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsCurrency;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsInstrument;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsNetwork;
import org.knowm.xchange.finerymarkets.dto.marketdata.response.InstrumentsResponse;
import org.knowm.xchange.finerymarkets.dto.trade.DealHistory;
import org.knowm.xchange.finerymarkets.dto.trade.OrderCreatedBy;
import org.knowm.xchange.finerymarkets.dto.trade.response.DealHistoryResponse;
import org.knowm.xchange.finerymarkets.streaming.dtos.FineryMarketsStreamingOrderResponseDto;
import org.knowm.xchange.finerymarkets.streaming.enums.Action;
import org.knowm.xchange.finerymarkets.streaming.enums.Feed;
import org.knowm.xchange.finerymarkets.streaming.exceptions.FailedToSubscribeException;

public class FineryMarketsDeserializationTest extends FineryMarketsTestBase {

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
        firstCurrency.getRawJson());

    assertEquals(3, instrumentsResponse.getInstruments().size());
    assertEquals("USDC-INR", firstInstrument.getName());
    assertEquals(563, firstInstrument.getId());
    assertEquals("USDC", firstInstrument.getAssetCurrencyName());
    assertEquals("INR", firstInstrument.getBalanceCurrencyName());
    assertEquals("[\"USDC-INR\",563,\"USDC\",\"INR\"]", firstInstrument.getRawJson());

    assertEquals(3, instrumentsResponse.getNetworks().size());
    assertEquals("XTZ", secondNetwork.getName());
    assertEquals("Tezos", secondNetwork.getDescription());
    assertEquals(28, secondNetwork.getId());
    assertEquals("[\"XTZ\",\"Tezos\",28]", secondNetwork.getRawJson());
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
    String response = IOUtils.resourceToString("/dealHistory_test_02.json", StandardCharsets.UTF_8);

    DealHistoryResponse dealHistoryResponse = mapper.readValue(response, DealHistoryResponse.class);

    assertEquals(17, dealHistoryResponse.getDeals().size());
    String expectedRawJson =
        "[\"BTC-USDT\",4,0,0,48221507535,0,0,100000000,0,1709716088000,1709716088000,1864944,0,6674859047619,85000000,5673630190500,0,87,0,0,1864943]";

    DealHistory expected =
        DealHistory.builder()
            .instrumentName("BTC-USDT")
            .orderType(OrderType.MARKET_IOC)
            .side(Side.BID)
            .cancelReason(CancelReason.IN_PLACE_OR_FILLED)
            .orderId(48221507535L)
            .clientOrderId(0)
            .orderPrice(0)
            .orderInitialSize(100000000)
            .remainingOrderSize(0)
            .orderCreatedAt(1709716088000L)
            .dealMoment(1709716088000L)
            .dealId(1864944)
            .dealAggressorSide(Side.BID)
            .dealPrice(6674859047619L)
            .dealSize(85000000)
            .dealVolume(5673630190500L)
            .dealDelta(0)
            .counterpartyId(87)
            .bySizeOrVolume(OrderCreatedBy.SIZE)
            .counterpartySubaccountId(0)
            .linkedDealId(1864943)
            .rawJson(expectedRawJson)
            .build();

    DealHistory actual = dealHistoryResponse.getDeals().get(0);
    assertEquals(expected, actual);
  }

  @Test
  public void testStreamingDealDeserialization_when_deal_response() throws IOException {
    String responseString = IOUtils.resourceToString("/streamingDealResponse_deal_01.json", StandardCharsets.UTF_8);

    FineryMarketsStreamingOrderResponseDto response =
        mapper.readValue(responseString, FineryMarketsStreamingOrderResponseDto.class);

    String expectedRawJson =
        "[\"BTC-USDT\",4,0,0,48221507535,0,0,100000000,0,1709716088000,1709716088000,1864944,0,6674859047619,85000000,5673630190500,0,87,0,0,1864943]";

    DealHistory expected =
        DealHistory.builder()
            .instrumentName("BTC-USDT")
            .orderType(OrderType.MARKET_IOC)
            .side(Side.BID)
            .cancelReason(CancelReason.IN_PLACE_OR_FILLED)
            .orderId(48221507535L)
            .clientOrderId(0)
            .orderPrice(0)
            .orderInitialSize(100000000)
            .remainingOrderSize(0)
            .orderCreatedAt(1709716088000L)
            .dealMoment(1709716088000L)
            .dealId(1864944)
            .dealAggressorSide(Side.BID)
            .dealPrice(6674859047619L)
            .dealSize(85000000)
            .dealVolume(5673630190500L)
            .dealDelta(0)
            .counterpartyId(87)
            .bySizeOrVolume(OrderCreatedBy.SIZE)
            .counterpartySubaccountId(0)
            .linkedDealId(1864943)
            .rawJson(expectedRawJson)
            .build();

    DealHistory actual = response.getDeals().get(0);
    assertEquals(expected, actual);
    assertEquals(Feed.ORDERS, response.getFeed());
    assertEquals("0", response.getFeedId());
    assertEquals(Action.NEW_DEAL, response.getAction());
  }

  @Test
  public void testStreamingDealDeserialization_when_snapshot_response() throws IOException {
    String responseString =
        IOUtils.resourceToString("/streamingDealResponse_snapshot_01.json", StandardCharsets.UTF_8);

    FineryMarketsStreamingOrderResponseDto response =
        mapper.readValue(responseString, FineryMarketsStreamingOrderResponseDto.class);
    assertEquals(Feed.ORDERS, response.getFeed());
    assertEquals("0", response.getFeedId());
    assertEquals(Action.SNAPSHOT, response.getAction());
    assertEquals(2, response.getDeals().size());
  }

  @Test
  public void testStreamingDealDeserialization_when_failed_subscribe_response() throws IOException {
    String responseString =
        IOUtils.resourceToString("/streamingDealResponse_failed_subscribe_01.json", StandardCharsets.UTF_8);

    assertThrows(
        FailedToSubscribeException.class,
        () -> mapper.readValue(responseString, FineryMarketsStreamingOrderResponseDto.class));
  }
}
