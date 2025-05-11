package info.bitrich.xchangestream.thalex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observable;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.enums.MarketParticipant;
import org.knowm.xchange.thalex.config.ThalexJacksonObjectMapperFactory;

public class ThalexStreamingTradeServiceTest {

  private ThalexStreamingService streamingService;
  private ThalexStreamingTradeService tradeService;
  private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    streamingService = mock(ThalexStreamingService.class);
    tradeService = new ThalexStreamingTradeService(streamingService);
    objectMapper = new ThalexJacksonObjectMapperFactory().createObjectMapper();
  }

  @Test
  public void testGetUserTrades() throws IOException {
    InputStream is = getClass().getResourceAsStream("/trade_history.json");
    assertNotNull("Resource not found", is);

    // Simulate raw JSON node
    JsonNode raw = objectMapper.readTree(is);
    doReturn(Observable.just(raw)).when(streamingService).subscribeChannel(any());
    List<UserTrade> trades = tradeService.getUserTrades().toList().blockingGet();

    assertEquals(2, trades.size());
    UserTrade trade = trades.get(0);

    assertEquals("0080000000000000184329278B2B94DB", trade.getId());
    assertEquals("001F3C2600000064", trade.getOrderId());
    assertEquals(new BigDecimal("0.028"), trade.getOriginalAmount());
    assertEquals(new BigDecimal("109100.0"), trade.getPrice());
    assertEquals(OrderType.ASK, trade.getType());
    assertEquals(new Currency("USD"), trade.getFeeCurrency());
    assertEquals(new BigDecimal("0.76"), trade.getFeeAmount());
    assertEquals(MarketParticipant.TAKER, trade.getMarketParticipant());
    assertEquals(Date.from(Instant.ofEpochSecond((long) 1.7482863301652372E9)),
        trade.getTimestamp());
    assertNotNull(trade.getRawJson());

  }
}