package info.bitrich.xchangestream.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.gemini.dto.trade.GeminiWsTradeResponse;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.trade.UserTrade;

@Slf4j
public class GeminiStreamingTradeService implements StreamingTradeService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  GeminiStreamingService streamingService;

  public GeminiStreamingTradeService(GeminiStreamingService streamingService) {
    this.streamingService = streamingService;
  }

  @Override
  public Observable<UserTrade> getUserTrades() {
    return streamingService
        .subscribeChannel(Constants.CHANNEL_TRADE_HISTORY)
        .map(n -> objectMapper.convertValue(n, GeminiWsTradeResponse.class))
        .map(GeminiStreamingAdapters::adaptUserTrade);
  }
}
