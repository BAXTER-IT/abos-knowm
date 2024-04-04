package org.knowm.xchange.finerymarkets.dto;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.knowm.xchange.finerymarkets.streaming.dtos.FineryMarketsStreamingOrderRequestDto;
import org.knowm.xchange.finerymarkets.streaming.enums.Event;

public class FineryMarketsSerializationTest extends FineryMarketsTestBase {

  @Test
  public void testOrderRequest_when_null_feedId_is_not_present() throws Exception {
    String expectedJson = "{\"event\":\"bind\",\"feed\":\"O\"}";
    FineryMarketsStreamingOrderRequestDto orderRequest =
        new FineryMarketsStreamingOrderRequestDto(Event.SUBSCRIBE);
    String actualJson = mapper.writeValueAsString(orderRequest);
    assertEquals(expectedJson, actualJson);
  }

  @Test
  public void testOrderRequest_when_feedId_is_present() throws Exception {
    String expectedJson = "{\"event\":\"bind\",\"feed\":\"O\",\"feedId\":\"BTC-EUR\"}";
    FineryMarketsStreamingOrderRequestDto orderRequest =
        new FineryMarketsStreamingOrderRequestDto("BTC-EUR");
    String actualJson = mapper.writeValueAsString(orderRequest);
    assertEquals(expectedJson, actualJson);
  }
}
