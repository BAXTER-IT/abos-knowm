package info.bitrich.xchangestream.finerymarkets;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import java.util.Objects;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.finerymarkets.FineryMarketsAdapters;
import org.knowm.xchange.finerymarkets.streaming.dtos.FineryMarketsStreamingOrderResponseDto;
import org.knowm.xchange.instrument.Instrument;

public class FineryMarketsStreamingTradeService implements StreamingTradeService {

  public static final String CHANNEL = "deals";
  private final ObjectMapper objectMapper = StreamingObjectMapperHelper.getObjectMapper();
  FineryMarketsStreamingService streamingService;

  public FineryMarketsStreamingTradeService(FineryMarketsStreamingService streamingService) {
    this.streamingService = streamingService;
  }

  @Override
  public Observable<UserTrade> getUserTrades(Instrument instrument, Object... args) {
    return getUserTrades();
  }

  @Override
  public Observable<UserTrade> getUserTrades(CurrencyPair currencyPair, Object... args) {
    return getUserTrades();
  }

  @Override
  public Observable<UserTrade> getUserTrades() {
    return streamingService
        .subscribeChannel(CHANNEL)
        .filter(message -> "D".equals(message.get(2).asText())) // new deal
        .map(
            s ->
                objectMapper.treeToValue(s, FineryMarketsStreamingOrderResponseDto.class).getDeal())
        .map(FineryMarketsAdapters::adaptUserTrade)
        .map(userTrade -> {
          // Invert side because we subscribe from the master point of view
          userTrade.invertType();
          return userTrade;
        });
  }
}
