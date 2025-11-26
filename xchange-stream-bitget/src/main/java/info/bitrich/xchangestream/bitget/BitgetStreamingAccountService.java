package info.bitrich.xchangestream.bitget;

import info.bitrich.xchangestream.bitget.dto.common.BitgetChannel.ChannelType;
import info.bitrich.xchangestream.bitget.dto.common.BitgetChannel.InstType;
import info.bitrich.xchangestream.bitget.dto.response.BitgetWsAccountNotification;
import info.bitrich.xchangestream.bitget.dto.response.BitgetWsPositionNotification;
import info.bitrich.xchangestream.core.StreamingAccountService;
import io.reactivex.Observable;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.instrument.Instrument;

@RequiredArgsConstructor
public class BitgetStreamingAccountService implements StreamingAccountService {

  private final BitgetStreamingService service;

  /**
   *
   * @param instrument Set it to null! Can be used when Bitget supports instId other than "default"
   * @param args       only arg should be an {@link InstType InstType}, defaults to USDT_FUTURES
   */
  @Override
  public Observable<OpenPosition> getPositionChanges(Instrument instrument, Object... args) {
    InstType instType = InstType.USDT_FUTURES;
    if (args.length > 0 && args[0] != null) {
      try {
        instType = (InstType) args[0];
      } catch (Exception ignore) {
      }
    }
    return service.subscribeChannel(null, ChannelType.POSITIONS, instType, instrument)
        .cast(BitgetWsPositionNotification.class)
        .flatMapIterable(BitgetWsPositionNotification::getPayloadItems)
        .map(BitgetStreamingAdapters::toOpenPosition);
  }

  /**
   *
   * @param instrument Set it to null! Can be used when Bitget supports instId other than "default"
   * @param args       only arg should be an {@link InstType InstType}, defaults to USDT_FUTURES
   */
  @Override
  public Observable<FundingRecord> getSpotLedgerChanges(Instrument instrument, Object... args) {
    return subscribePositions(instrument, InstType.SPOT);
  }

  @Override
  public Observable<FundingRecord> getFuturesLedgerChanges(Instrument instrument, Object... args) {
    InstType instType = InstType.USDT_FUTURES;
    if (args.length > 0 && args[0] != null) {
      try {
        instType = (InstType) args[0];
      } catch (Exception ignore) {
      }
    }
    return subscribePositions(instrument, instType);
  }

  private Observable<FundingRecord> subscribePositions(Instrument instrument, InstType instType) {
    return service.subscribeChannel(null, ChannelType.ACCOUNT, instType, instrument, true)
        .cast(BitgetWsAccountNotification.class)
        .flatMapIterable(BitgetWsAccountNotification::getPayloadItems)
        .map(BitgetStreamingAdapters::toFundingRecord)
        .filter(Objects::nonNull);
  }
}
