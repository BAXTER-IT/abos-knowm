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
import org.knowm.xchange.finerymarkets.dto.marketdata.response.FineryMarketsInstrumentsResponse;

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

    FineryMarketsInstrumentsResponse instrumentsResponse =
        mapper.readValue(response, FineryMarketsInstrumentsResponse.class);

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

    FineryMarketsInstrumentsResponse instrumentsResponse =
        mapper.readValue(response, FineryMarketsInstrumentsResponse.class);

    assertNotNull(instrumentsResponse.getNetworks());
    assertTrue(instrumentsResponse.getNetworks().isEmpty());
  }
}
