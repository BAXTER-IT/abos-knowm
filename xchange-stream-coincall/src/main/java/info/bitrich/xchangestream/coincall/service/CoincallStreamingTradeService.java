package info.bitrich.xchangestream.coincall.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.coincall.CoincallStreamingAdapters;
import info.bitrich.xchangestream.coincall.CoincallStreamingService;
import info.bitrich.xchangestream.coincall.dto.CoincallWsResponseDto;
import info.bitrich.xchangestream.coincall.dto.trade.CoincallWsTradeDto;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.dto.trade.UserTrade;

@RequiredArgsConstructor
public class CoincallStreamingTradeService implements StreamingTradeService {

  private final ObjectMapper objectMapper = StreamingObjectMapperHelper.getObjectMapper();

  private final CoincallStreamingService spotStreamingService;

  @Override
  public Observable<UserTrade> getUserTrades() {
    return spotStreamingService.subscribeChannel("trade")
        .map(o -> objectMapper.convertValue(o,
            new TypeReference<CoincallWsResponseDto<CoincallWsTradeDto>>() {
            }).getData())
        .map(CoincallStreamingAdapters::toUserTrade);
  }
}
