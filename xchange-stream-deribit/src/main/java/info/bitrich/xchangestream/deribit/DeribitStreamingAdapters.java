package info.bitrich.xchangestream.deribit;

import info.bitrich.xchangestream.deribit.dto.response.DeribitUserChangeNotification;
import info.bitrich.xchangestream.deribit.dto.response.DeribitUserTradeNotification;
import info.bitrich.xchangestream.deribit.dto.response.DeribitUserTradeNotification.UserTradeData;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.knowm.xchange.deribit.v2.DeribitAdapters;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.account.OpenPosition.MarginMode;
import org.knowm.xchange.dto.trade.UserTrade;

@UtilityClass
public class DeribitStreamingAdapters {

  public UserTrade toUserTrade(DeribitUserTradeNotification notification) {
    UserTradeData userTradeData = notification.getParams().getData().get(0);
    return UserTrade.builder()
        .orderId(userTradeData.getOrderId())
        .feeAmount(userTradeData.getFee())
        .feeCurrency(userTradeData.getFeeCurrency())
        .orderUserReference(userTradeData.getLabel())
        .type(userTradeData.getOrderSide())
        .originalAmount(userTradeData.getAmount())
        .instrument(DeribitAdapters.toInstrument(userTradeData.getInstrumentName()))
        .price(userTradeData.getPrice())
        .timestamp(DeribitAdapters.toDate(userTradeData.getTimestamp()))
        .id(userTradeData.getTradeId())
        .build();
  }

  public OpenPosition toOpenPosition(DeribitUserChangeNotification notification) {
    var deribitPosition = Optional.ofNullable(notification.getParams().getData().getPositions())
        .map(deribitPositions -> deribitPositions.get(0))
        .orElse(null);

    if (deribitPosition == null) {
      return null;
    }

    var size = deribitPosition.getSizeCurrency() != null ? deribitPosition.getSizeCurrency() : deribitPosition.getSize();
    return OpenPosition.builder()
        .instrument(DeribitAdapters.toInstrument(deribitPosition.getInstrumentName()))
        .type(deribitPosition.getPositionType())
        .marginMode(MarginMode.CROSS)
        .size(size)
        .price(deribitPosition.getAveragePrice())
        .unRealisedPnl(deribitPosition.getFloatingProfitLoss())
        .build();
  }


}
