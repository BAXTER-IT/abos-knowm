package org.knowm.xchange.finerymarkets.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.finerymarkets.FineryMarketsAuthenticated;
import org.knowm.xchange.finerymarkets.FineryMarketsExchange;
import org.knowm.xchange.finerymarkets.dto.DecoratedPayload;
import org.knowm.xchange.finerymarkets.dto.trade.response.DealHistoryResponse;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;

public class FineryMarketsTradeServiceTest {

  ObjectMapper objectMapper = new ObjectMapper();

  FineryMarketsExchange exchange;
  FineryMarketsAuthenticated fineryMarketsAuthenticatedMock =
      mock(FineryMarketsAuthenticated.class);

  @Before
  public void setUp() throws Exception {
    exchange = spy(ExchangeFactory.INSTANCE.createExchange(FineryMarketsExchange.class));
  }

  @Test
  public void getTradeHistory() throws Exception {
    String response = IOUtils.resourceToString("/dealHistory_test_02.json", StandardCharsets.UTF_8);

    DealHistoryResponse dealHistoryResponseFeed =
        objectMapper.readValue(response, DealHistoryResponse.class);

    DealHistoryResponse dealHistoryResponse =
        new DealHistoryResponse() {
          @Override
          protected int getMaxDealHistorySize() {
            return 17;
          }
        };

    dealHistoryResponse.setDeals(dealHistoryResponseFeed.getDeals());

    FineryMarketsTradeService tradeService = (FineryMarketsTradeService) exchange.getTradeService();
    setMock(
        FineryMarketsBaseService.class.getDeclaredField("fineryMarketsAuthenticated"),
        tradeService,
        fineryMarketsAuthenticatedMock);

    when(fineryMarketsAuthenticatedMock.getDealHistory(any(), any(), any()))
        .thenAnswer(
            invocation -> {
//              Object[] args = invocation.getArguments();
//              DecoratedPayload payload = (DecoratedPayload) args[2];

              DecoratedPayload payload = invocation.getArgument(2);
              if (payload.getData().get("till") == null) {
                return dealHistoryResponse;
              }
              dealHistoryResponse.setDeals(dealHistoryResponseFeed.getDeals().subList(0, 3));
              return dealHistoryResponse;
            });

    TradeHistoryParamsAll params = new TradeHistoryParamsAll();
    UserTrades tradeHistory = exchange.getTradeService().getTradeHistory(params);

    verify(fineryMarketsAuthenticatedMock, times(2)).getDealHistory(any(), any(), any());

    // 17 from first response + 3 from second response
    assertEquals(20, tradeHistory.getUserTrades().size());
  }

  private void setMock(Field field, Object instance, Object newValue) throws Exception {
    field.setAccessible(true);
    field.set(instance, newValue);
  }
}
