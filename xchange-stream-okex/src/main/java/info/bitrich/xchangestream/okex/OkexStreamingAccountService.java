package info.bitrich.xchangestream.okex;

import static info.bitrich.xchangestream.okex.OkexStreamingService.BALANCE_AND_POSITION;
import static info.bitrich.xchangestream.okex.OkexStreamingService.POSITIONS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.okex.dto.OkexStreamDto;
import info.bitrich.xchangestream.okex.dto.accont.OkexWsLedgerDto;
import info.bitrich.xchangestream.okex.dto.enums.OkexEventType;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import java.util.List;
import java.util.Objects;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.okex.OkexAdapters;
import org.knowm.xchange.okex.dto.account.OkexPosition;

public class OkexStreamingAccountService implements StreamingAccountService {

  private final OkexStreamingService service;
  private final ExchangeMetaData exchangeMetaData;
  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  public OkexStreamingAccountService(OkexStreamingService service,
      ExchangeMetaData exchangeMetaData) {
    this.service = service;
    this.exchangeMetaData = exchangeMetaData;
  }

  @Override
  public Observable<OpenPosition> getPositionChanges(Instrument instrument, Object... args) {
    return service.subscribeChannel(POSITIONS + instrument)
        .filter(message -> message.has("data"))
        .map(node ->
            mapper.convertValue(node,
                new TypeReference<OkexStreamDto<List<OkexPosition>>>() {
                }))
        .flatMapIterable(OkexStreamDto::getData)
        .map(data -> OkexAdapters.adaptOpenPosition(data, exchangeMetaData));
  }

  @Override
  public Observable<FundingRecord> getSpotLedgerChanges(Instrument instrument, Object... args) {
    return service.subscribeChannel(BALANCE_AND_POSITION + instrument)
        .filter(message -> message.has("data"))
        .map(node ->
            mapper.convertValue(node,
                new TypeReference<OkexStreamDto<List<OkexWsLedgerDto>>>() {
                }))
        .flatMapIterable(OkexStreamDto::getData)
        .filter(ledger -> !OkexEventType.SNAPSHOT.equals(
            ledger.getEventType())) // only interested in events
        .flatMapIterable(OkexWsLedgerDto::getBalanceData)
        .map(OkexStreamingAdapters::adaptFundingRecord)
        .filter(Objects::nonNull);
  }
}
