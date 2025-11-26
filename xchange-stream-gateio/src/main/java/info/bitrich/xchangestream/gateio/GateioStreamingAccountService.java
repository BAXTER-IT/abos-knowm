package info.bitrich.xchangestream.gateio;

import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.gateio.config.Config;
import info.bitrich.xchangestream.gateio.dto.response.balance.GateioSingleSpotBalanceNotification;
import io.reactivex.rxjava3.core.Observable;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;
import info.bitrich.xchangestream.gateio.dto.response.GateioFuturePositionsNotification;
import info.bitrich.xchangestream.gateio.dto.response.balance.GateioFuturesBalancesNotification;
import info.bitrich.xchangestream.gateio.dto.response.balance.GateioSpotBalancesNotification;
import io.reactivex.Observable;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.instrument.Instrument;

@RequiredArgsConstructor
public class GateioStreamingAccountService implements StreamingAccountService {

  private final GateioStreamingService service;
  private final GateioStreamingService perpetualFutureService;
  private final GateioStreamingService deliveryFutureService;

  @Override
  public Observable<OpenPosition> getPositionChanges(Instrument instrument, Object... args) {
    return Observable.merge(
        subscribePositions(perpetualFutureService, instrument, Config.PRODUCT_PERPETUAL_FUTURES),
        subscribePositions(deliveryFutureService, instrument, Config.PRODUCT_DELIVERY_FUTURES)
    );
  }

  private Observable<OpenPosition> subscribePositions(GateioStreamingService service,
      Instrument instrument, String product) {
    return service.subscribeChannel(Config.CHANNEL_FUTURES_POSITIONS, instrument, product)
        .cast(GateioFuturePositionsNotification.class)
        .flatMapIterable(GateioFuturePositionsNotification::getResult)
        .map(GateioStreamingAdapters::toOpenPosition);
  }

  @Override
  public Observable<Balance> getBalanceChanges(Currency currency, Object... args) {
    return service
        .subscribeChannel(Config.SPOT_BALANCES_CHANNEL)
        .map(GateioSingleSpotBalanceNotification.class::cast)
        .filter(
            notification ->
                (currency == null) || (notification.getResult().getCurrency().equals(currency)))
        .map(GateioStreamingAdapters::toBalance);
  }

  @Override
  public Observable<FundingRecord> getSpotLedgerChanges(Instrument instrument, Object... args) {
    return service.subscribeChannel(Config.CHANNEL_SPOT_BALANCES, instrument, Config.PRODUCT_SPOT)
        .cast(GateioSpotBalancesNotification.class)
        .flatMapIterable(GateioSpotBalancesNotification::getResult)
        .map(GateioStreamingAdapters::toBalance);
  }

  @Override
  public Observable<FundingRecord> getFuturesLedgerChanges(Instrument instrument, Object... args) {
    return Observable.merge(
        subscribeFuturesBalance(perpetualFutureService, instrument,
            Config.PRODUCT_PERPETUAL_FUTURES),
        subscribeFuturesBalance(deliveryFutureService, instrument, Config.PRODUCT_DELIVERY_FUTURES)
    );
  }

  private Observable<FundingRecord> subscribeFuturesBalance(
      GateioStreamingService service, Instrument instrument, String product) {
    return service.subscribeChannel(Config.CHANNEL_FUTURES_BALANCES, instrument, product)
        .cast(GateioFuturesBalancesNotification.class)
        .flatMapIterable(GateioFuturesBalancesNotification::getResult)
        .map(GateioStreamingAdapters::toBalance);
}
