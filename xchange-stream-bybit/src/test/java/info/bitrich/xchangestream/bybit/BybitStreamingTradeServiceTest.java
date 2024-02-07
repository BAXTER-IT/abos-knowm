package info.bitrich.xchangestream.bybit;

import static info.bitrich.xchangestream.bybit.BybitStreamingTradeService.EXECUTION_CHANNEL;
import static org.junit.Assert.*;
import static org.knowm.xchange.Exchange.USE_SANDBOX;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.bybit.dto.BybitUserTradeResponseDto;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bybit.BybitExchange;
import org.knowm.xchange.bybit.config.BybitJacksonObjectMapperFactory;
import org.knowm.xchange.bybit.dto.trade.BybitOrderType;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.enums.MarketParticipant;

public class BybitStreamingTradeServiceTest {

  private BybitStreamingExchange exchange;

  @Before
  public void setUp() {
    ExchangeSpecification exchangeSpecification =
        new BybitExchange().getDefaultExchangeSpecification();

    exchangeSpecification.setApiKey("apikey");
    exchangeSpecification.setSecretKey("secret");
    exchangeSpecification.setSslUri("http://localhost");
    exchangeSpecification.setHost("bybit.com");
    exchangeSpecification.setPort(6789);
    exchangeSpecification.setShouldLoadRemoteMetaData(false);
    exchangeSpecification.setExchangeSpecificParametersItem(USE_SANDBOX, true);

    exchange =
        (BybitStreamingExchange)
            StreamingExchangeFactory.INSTANCE.createExchangeWithoutSpecification(
                BybitStreamingExchange.class);
    exchange.applySpecification(exchangeSpecification);
  }

  @Test
  public void getUserTradesTest_when_stopOrderType_in_response_is_empty_string() throws Exception {

    String json = IOUtils.resourceToString("/websocketTrade.json", StandardCharsets.UTF_8);

    exchange.getStreamingTradeService().streamingService = mock(BybitStreamingService.class);

    doReturn(Observable.just(createJsonNode(json)))
        .when(exchange.getStreamingTradeService().streamingService)
        .subscribeChannel(anyString());

    TestObserver<UserTrade> testObserver =
        exchange.getStreamingTradeService().getUserTrades().test();

    testObserver.awaitTerminalEvent();
    testObserver
        .assertNoErrors()
        .assertValueCount(1)
        .assertOf(
            userTrade -> {
              UserTrade trade = userTrade.values().iterator().next();
              assertEquals(OrderType.ASK, trade.getType());
              assertEquals(CurrencyPair.BTC_USDT.toString(), trade.getInstrument().toString());
              assertEquals(MarketParticipant.TAKER, trade.getMarketParticipant());
            });

    verify(exchange.getStreamingTradeService().streamingService)
        .subscribeChannel(EXECUTION_CHANNEL);
  }

  @Test
  public void testObjectMapper_when_parsing_empty_string_to_enum() throws IOException {
    ObjectMapper objectMapper = StreamingObjectMapperHelper.getObjectMapper();
    BybitJacksonObjectMapperFactory factory = new BybitJacksonObjectMapperFactory();
    factory.configureObjectMapper(objectMapper);

    String json = IOUtils.resourceToString("/websocketTrade.json", StandardCharsets.UTF_8);

    BybitUserTradeResponseDto bybitUserTradeResponseDto =
        objectMapper.treeToValue(createJsonNode(json), BybitUserTradeResponseDto.class);
    assertEquals(BybitOrderType.MARKET, bybitUserTradeResponseDto.getData().get(0).getOrderType());
    assertEquals(BybitOrderType.UNKNOWN, bybitUserTradeResponseDto.getData().get(0).getStopOrderType());
  }

  private JsonNode createJsonNode(String json) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(json);
  }
}
