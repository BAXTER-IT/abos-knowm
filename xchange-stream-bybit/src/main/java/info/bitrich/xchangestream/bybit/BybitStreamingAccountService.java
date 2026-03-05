package info.bitrich.xchangestream.bybit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.bybit.dto.BybitWsResponseDto;
import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.rxjava3.core.Observable;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.bybit.BybitAdapters;
import org.knowm.xchange.bybit.dto.account.BybitPosition;
import org.knowm.xchange.bybit.dto.account.walletbalance.BybitAccountBalance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.instrument.Instrument;

@RequiredArgsConstructor
public class BybitStreamingAccountService implements StreamingAccountService {

  public static final String CHANNEL_POSITION = "position";
  public static final String CHANNEL_WALLET = "wallet";
  private final ObjectMapper objectMapper = StreamingObjectMapperHelper.getObjectMapper();

  private final BybitStreamingService service;

  @Override
  public Observable<OpenPosition> getPositionChanges(Instrument instrument, Object... args) {
    return service.subscribeChannel(CHANNEL_POSITION)
        .filter(jsonNode -> CHANNEL_POSITION.equals(jsonNode.path("topic").asText(null)))
        .map(node -> objectMapper.convertValue(
            node,
            new TypeReference<BybitWsResponseDto<BybitPosition>>() {
            }
        ))
        .flatMapIterable(BybitWsResponseDto::getData)
        .map(BybitAdapters::adaptBybitPosition)
        .filter(Objects::nonNull);
  }

  @Override
  public Observable<FundingRecord> getSpotLedgerChanges(Instrument instrument, Object... args) {
    return service.subscribeChannel(CHANNEL_WALLET)
        .filter(jsonNode -> CHANNEL_WALLET.equals(jsonNode.path("topic").asText(null)))
        .map(node -> objectMapper.convertValue(
            node,
            new TypeReference<BybitWsResponseDto<BybitAccountBalance>>() {
            }
        ))
        .flatMapIterable(BybitWsResponseDto::getData)
        .flatMapIterable(BybitAccountBalance::getCoins)
        .map(BybitAdapters::adaptBybitAccountBalance)
        .filter(Objects::nonNull);
  }
}
