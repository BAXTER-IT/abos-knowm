package info.bitrich.xchangestream.coincall.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.coincall.CoincallStreamingAdapters;
import info.bitrich.xchangestream.coincall.CoincallStreamingService;
import info.bitrich.xchangestream.coincall.dto.CoincallWsDerivativeResponseDto;
import info.bitrich.xchangestream.coincall.dto.account.CoincallWsFuturesPositionDto;
import info.bitrich.xchangestream.coincall.dto.account.CoincallWsOptionsPositionDto;
import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.instrument.Instrument;

@RequiredArgsConstructor
public class CoincallStreamingAccountService implements StreamingAccountService {

  private final ObjectMapper objectMapper = StreamingObjectMapperHelper.getObjectMapper();

  private final CoincallStreamingService futuresStreamingService;
  private final CoincallStreamingService optionsStreamingService;

  @Override
  public Observable<OpenPosition> getPositionChanges(Instrument instrument, Object... args) {
    return Observable.merge(
        subscribeFuturesPositions(futuresStreamingService),
        subscribeOptionsPositions(optionsStreamingService)
    );
  }

  private ObservableSource<OpenPosition> subscribeFuturesPositions(
      CoincallStreamingService service) {
    return service.subscribeChannel("positionEvent", "futures")
        .map(o -> objectMapper.convertValue(o,
            new TypeReference<CoincallWsDerivativeResponseDto<CoincallWsFuturesPositionDto>>() {
            }).getData())
        .map(CoincallStreamingAdapters::toOpenPosition);
  }

  private ObservableSource<OpenPosition> subscribeOptionsPositions(
      CoincallStreamingService service) {
    return service.subscribeChannel("positionEvent", "options")
        .map(o -> objectMapper.convertValue(o,
            new TypeReference<CoincallWsDerivativeResponseDto<CoincallWsOptionsPositionDto>>() {
            }).getData())
        .map(CoincallStreamingAdapters::toOpenPosition);
  }
}
