package info.bitrich.xchangestream.kucoin;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsFuturesBalanceEvent;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsPositionsEvent;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsSpotBalanceEvent;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.rxjava3.core.Observable;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;
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
  public static final String SUBJECT_POSITION_SETTLEMENT = "position.settlement";
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

  public Observable<KucoinWsPositionsEvent> getSymbolPositionChanges(String symbol) {
    return  service
        .subscribeChannel("/contract/position:" + symbol)
        .filter(jsonNode -> SUBJECT_POSITION_SETTLEMENT.equals(jsonNode.path("subject").asText(null)))
        .map(node -> mapper.convertValue(node, KucoinWsPositionsEvent.class));
  }

  /**
   * Spot balances for the logged-in user.
   *
   * <p>Distinct from {@link #getSpotLedgerChanges}, which adapts the same channel to
   * a {@link FundingRecord} for funding-history callers and so reports the change
   * rather than the resulting balance. This reports the total, available and held
   * amounts as they stand after each change.
   *
   * <p>Filtered to {@code currency} when one is given, all currencies when null. The
   * exchange emits one frame per currency and account rather than a full snapshot,
   * so a caller should apply each frame to the currency it names and must not treat
   * one as a complete picture.
   */
  @Override
  public Observable<Balance> getBalanceChanges(Currency currency, Object... args) {
    return getRawSpotBalanceChanges()
        .map(event -> KucoinStreamingAdapters.adaptBalance(event.getData()))
        .filter(balance -> currency == null || currency.equals(balance.getCurrency()));
  }

  /**
   * Futures wallet balances for the logged-in user, with the wallet balance as the
   * total.
   *
   * <p>Requires an exchange whose base URI points at the futures host, since the
   * channel is served only there.
   */
  public Observable<Balance> getFuturesBalanceChanges(Currency currency) {
    return getRawFuturesWalletChanges()
        .map(event -> KucoinStreamingAdapters.adaptBalance(event.getData()))
        .filter(balance -> currency == null || currency.equals(balance.getCurrency()));
  }

  /**
   * Raw spot balance frames.
   *
   * <p>For callers needing what {@link Balance} cannot carry — chiefly
   * {@code relationEvent}, which identifies the account the change occurred in
   * (main, trade, margin, isolated). The channel covers every account type of the
   * logged-in user, and one currency exists in several of them at once, so a frame
   * stripped of that field cannot be attributed to an account.
   */
  public Observable<KucoinWsSpotBalanceEvent> getRawSpotBalanceChanges() {
    return service.subscribeChannel("/account/balance")
        .filter(jsonNode -> SUBJECT_SPOT_BALANCE.equals(jsonNode.path("subject").asText(null)))
        .map(jsonNode -> mapper.convertValue(jsonNode, KucoinWsSpotBalanceEvent.class));
  }

  /**
   * Raw futures wallet frames.
   *
   * <p>For callers needing what {@link Balance} cannot carry — the cross and
   * isolated margin breakdown, unrealised profit and loss, and {@code version},
   * which the exchange increments on every change and is therefore the only way to
   * detect a frame that never arrived. The spot channel carries no equivalent.
   */
  public Observable<KucoinWsFuturesBalanceEvent> getRawFuturesWalletChanges() {
    return service.subscribeChannel("/contractAccount/wallet")
        .filter(jsonNode -> SUBJECT_FUTURES_BALANCE.equals(jsonNode.path("subject").asText(null)))
        .map(jsonNode -> mapper.convertValue(jsonNode, KucoinWsFuturesBalanceEvent.class));
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
