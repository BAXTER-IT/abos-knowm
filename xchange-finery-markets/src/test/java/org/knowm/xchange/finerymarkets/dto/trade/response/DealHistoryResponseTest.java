package org.knowm.xchange.finerymarkets.dto.trade.response;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class DealHistoryResponseTest {

  ObjectMapper objectMapper = new ObjectMapper();
  DealHistoryResponse dealHistoryResponse;

  @Before
  public void setUp() throws Exception {
    String response = IOUtils.resourceToString("/dealHistory_test_02.json", StandardCharsets.UTF_8);
    dealHistoryResponse = objectMapper.readValue(response, DealHistoryResponse.class);
  }

  @Test
  public void getTillDealId_when_response_is_not_empty() {
    assertEquals(1051761, dealHistoryResponse.getTillDealId().longValue());
  }

  @Test
  public void getTillDealId_null_when_response_is_empty() {
    DealHistoryResponse dealHistoryResponse2 = new DealHistoryResponse();
    assertNull(dealHistoryResponse2.getTillDealId());
  }

  @Test
  public void isFullPage_false_when_response_size_is_less_than_max_page_size() {
    assertFalse(dealHistoryResponse.isFullPage());
  }

  @Test
  public void isFullPage_true_when_response_size_and_max_page_size_is_equal() {
    DealHistoryResponse dealHistoryResponse2 =
        new DealHistoryResponse() {
          @Override
          protected int getMaxDealHistorySize() {
            return 17;
          }
        };
    dealHistoryResponse2.setDeals(dealHistoryResponse.getDeals());
    assertTrue(dealHistoryResponse2.isFullPage());
  }
}
