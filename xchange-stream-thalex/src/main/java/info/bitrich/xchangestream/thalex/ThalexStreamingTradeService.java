package info.bitrich.xchangestream.thalex;

import static org.knowm.xchange.thalex.dto.enums.ThalexTradeType.NORMAL;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.thalex.dto.ThalexNotification;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.thalex.ThalexAdapters;
import org.knowm.xchange.thalex.config.ThalexJacksonObjectMapperFactory;
import org.knowm.xchange.thalex.dto.trade.ThalexTradeDto;

@Slf4j
public class ThalexStreamingTradeService implements StreamingTradeService {

  private final ObjectMapper objectMapper = new ThalexJacksonObjectMapperFactory().createObjectMapper();
  ThalexStreamingService streamingService;

  public ThalexStreamingTradeService(ThalexStreamingService streamingService) {
    this.streamingService = streamingService;
  }

  @Override
  public Observable<UserTrade> getUserTrades() {
    return streamingService
        .subscribeChannel(Constants.CHANNEL_TRADE_HISTORY)
        .map(
            s ->
                objectMapper.convertValue(s,
                    new TypeReference<ThalexNotification<List<ThalexTradeDto>>>() {
                    }))
        .flatMapIterable(ThalexNotification::getNotification)
        .filter(thalexTradeDto -> NORMAL.equals(thalexTradeDto.getTradeType()))
        .map(ThalexAdapters::toUserTrade);
  }
}
