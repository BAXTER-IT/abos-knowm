package info.bitrich.xchangestream.gemini;

import static org.knowm.xchange.gemini.GeminiAdapters.adaptInstrument;

import info.bitrich.xchangestream.gemini.dto.enums.GeminiWsTradeSide;
import info.bitrich.xchangestream.gemini.dto.trade.GeminiWsTradeResponse;
import java.util.Date;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.enums.MarketParticipant;

public final class GeminiStreamingAdapters {


  public static UserTrade adaptUserTrade(GeminiWsTradeResponse in) {
    OrderType orderType =
        GeminiWsTradeSide.BUY.equals(in.getSide()) ? OrderType.BID : OrderType.ASK;

    Date date = new Date(in.getTimestampms());

    MarketParticipant marketParticipant = "maker".equalsIgnoreCase(in.getFill().getLiquidity()) ? MarketParticipant.MAKER : MarketParticipant.TAKER;

    return UserTrade.builder()
        .type(orderType)
        .originalAmount(in.getFill().getAmount())
        .instrument(adaptInstrument(in.getSymbol()))
        .price(in.getFill().getPrice())
        .timestamp(date)
        .id(String.valueOf(in.getFill().getTradeId()))
        .orderId(in.getOrderId())
        .orderUserReference(in.getClientOrderId())
        .feeAmount(in.getFill().getFee())
        .feeCurrency(Currency.getInstance(in.getFill().getFeeCurrency()))
        .marketParticipant(marketParticipant)
        .rawJson(in.getRawJson())
        .build();
  }
}
