package info.bitrich.xchangestream.kucoin;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsFuturesBalanceEvent;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsPositionsEvent;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsSpotBalanceEvent;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.instrument.Instrument;

/**
 * When creating KucoinStreamingExchange and applying the ExchangeSpecification, you need to
 * setSslUri to “https://api-futures.kucoin.com” (by default it is creating the exchange with Spot
 * base URL)
 */
@RequiredArgsConstructor
public class KucoinStreamingAccountService implements StreamingAccountService {

  public static final String SUBJECT_POSITION_CHANGE = "position.change";
  public static final String SUBJECT_SPOT_BALANCE = "account.balance";
  public static final String SUBJECT_FUTURES_BALANCE = "walletBalance.change";

  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  private final KucoinStreamingService service;

  @Override
  public Observable<OpenPosition> getPositionChanges(Instrument instrument, Object... args) {
    return service.subscribeChannel("/contract/positionAll")
        .filter(jsonNode -> SUBJECT_POSITION_CHANGE.equals(jsonNode.path("subject").asText(null)))
        .map(jsonNode -> mapper.convertValue(jsonNode, KucoinWsPositionsEvent.class))
        .map(event -> KucoinStreamingAdapters.adaptOpenPosition(event.getData()));
  }

  @Override
  public Observable<FundingRecord> getSpotLedgerChanges(Instrument instrument, Object... args) {
    return service.subscribeChannel("/account/balance")
        .filter(jsonNode -> SUBJECT_SPOT_BALANCE.equals(jsonNode.path("subject").asText(null)))
        .map(jsonNode -> mapper.convertValue(jsonNode, KucoinWsSpotBalanceEvent.class))
        .map(event -> KucoinStreamingAdapters.adaptFundingRecord(event.getData()));
  }

  @Override
  public Observable<FundingRecord> getFuturesLedgerChanges(Instrument instrument, Object... args) {
    return service.subscribeChannel("/contractAccount/wallet")
        .filter(jsonNode -> SUBJECT_FUTURES_BALANCE.equals(jsonNode.path("subject").asText(null)))
        .map(jsonNode -> mapper.convertValue(jsonNode, KucoinWsFuturesBalanceEvent.class))
        .map(event -> KucoinStreamingAdapters.adaptFundingRecord(event.getData()));
  }
}
