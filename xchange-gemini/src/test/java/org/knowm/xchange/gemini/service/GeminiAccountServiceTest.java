package org.knowm.xchange.gemini.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.gemini.GeminiExchange;
import org.knowm.xchange.gemini.dto.account.GeminiTransferResponse;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeminiAccountServiceTest {

  private GeminiExchange exchange;

  private GeminiAccountService service;

  @Before
  public void setup() {
    exchange = new GeminiExchange() {
      @Override
      protected void initServices() {
        accountService = new GeminiAccountService(this);
      }
    };
    ExchangeSpecification specification = exchange.getDefaultExchangeSpecification();
    specification.setShouldLoadRemoteMetaData(false);
    exchange.applySpecification(specification);
    service = new GeminiAccountService(exchange);
  }

  @Test
  public void testGetFundingHistory_basicFlow() throws Exception {
    TradeHistoryParams params = mock(TradeHistoryParams.class);

    GeminiTransferResponse transfer1 = GeminiTransferResponse.builder()
        .timestampms(123456789L)
        .currency("USD")
        .build();
    GeminiTransferResponse transfer2 = GeminiTransferResponse.builder()
        .timestampms(123456790L)
        .currency("USD")
        .build();

    List<GeminiTransferResponse> batch = Arrays.asList(transfer1, transfer2);

    GeminiAccountService spyService = spy(service);

    doReturn(batch).doReturn(Collections.emptyList())
        .when(spyService).getGeminiTransfers(any(), any(), any());

    List<FundingRecord> result = spyService.getFundingHistory(params);

    assertEquals(2, result.size());
    assertEquals(new Date(transfer1.getTimestampms()), result.get(0).getDate());
    assertEquals(new Currency(transfer1.getCurrency()), result.get(0).getCurrency());
    assertEquals(new Date(transfer2.getTimestampms()), result.get(1).getDate());
    assertEquals(new Currency(transfer2.getCurrency()), result.get(1).getCurrency());
  }

}