package info.bitrich.xchangestream.coincall;

import info.bitrich.xchangestream.coincall.dto.account.CoincallWsFuturesPositionDto;
import info.bitrich.xchangestream.coincall.dto.account.CoincallWsOptionsPositionDto;
import info.bitrich.xchangestream.coincall.dto.enums.CoincallMakerTaker;
import info.bitrich.xchangestream.coincall.dto.enums.CoincallPositionSide;
import info.bitrich.xchangestream.coincall.dto.trade.CoincallWsTradeDto;
import java.math.BigDecimal;
import java.util.Date;
import org.knowm.xchange.coincall.dtos.enums.CoincallTradeSide;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.account.OpenPosition.Type;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.enums.MarketParticipant;

public class CoincallStreamingAdapters {

  public static UserTrade toUserTrade(CoincallWsTradeDto in) {

    CurrencyPair pair = parseSpotSymbol(in.getSymbol());

    OrderType side =
        in.getTradeSide() == CoincallTradeSide.BUY
            ? OrderType.BID
            : OrderType.ASK;

    BigDecimal amount = in.getVolume();
    BigDecimal price = in.getPrice();
    BigDecimal fee = in.getTradeFee();
    Currency feeCurrency = pair.getCounter(); // fee is always in quote currency

    return UserTrade.builder()
        .type(side)
        .originalAmount(amount)
        .instrument(pair)
        .price(price)
        .timestamp(new Date(in.getTimestamp()))
        .id(in.getTradeId())
        .orderId(String.valueOf(in.getOrderId()))
        .feeAmount(fee)
        .feeCurrency(feeCurrency)
        .marketParticipant(CoincallMakerTaker.MAKER.equals(in.getMakerTaker())
            ? MarketParticipant.MAKER
            : MarketParticipant.TAKER)
        .build();
  }


  private static CurrencyPair parseSpotSymbol(String symbol) {
    if (symbol == null || symbol.isEmpty()) {
      throw new IllegalArgumentException("Symbol must not be null or empty");
    }

    String upper = symbol.toUpperCase();
    String quote = "USDT";

    if (!upper.endsWith(quote)) {
      throw new IllegalArgumentException(
          "Spot symbol does not end with USDT as expected: " + symbol);
    }

    String base = upper.substring(0, upper.length() - quote.length());
    if (base.isEmpty()) {
      throw new IllegalArgumentException("Spot symbol has empty base: " + symbol);
    }

    return new CurrencyPair(base, quote);
  }

  private static CurrencyPair parseDerivativeSymbol(String symbol) {
    if (symbol == null || symbol.isEmpty()) {
      throw new IllegalArgumentException("Symbol must not be null or empty");
    }

    String upper = symbol.toUpperCase();
    String quote = "USD";

    int idx = upper.indexOf(quote);
    if (idx <= 0) { // must be at least one char of base before "USD"
      throw new IllegalArgumentException(
          "Derivative symbol does not contain base+USD as expected: " + symbol);
    }

    String base = upper.substring(0, idx);
    return new CurrencyPair(base, quote);
  }

  public static OpenPosition toOpenPosition(CoincallWsFuturesPositionDto in) {
    return new OpenPosition.Builder()
        .instrument(parseDerivativeSymbol(in.getSymbol()))
        .type(CoincallPositionSide.LONG.equals(in.getSide()) ? Type.LONG : Type.SHORT)
        .size(in.getQuantity())
        .price(in.getAveragePrice())
        .liquidationPrice(in.getEstimatedLiquidationPrice())
        .unRealisedPnl(in.getUnrealisedPnlLastPrice()).build();
  }

  public static OpenPosition toOpenPosition(CoincallWsOptionsPositionDto in) {
    return new OpenPosition.Builder()
        .instrument(parseDerivativeSymbol(in.getSymbol()))
        .type(CoincallPositionSide.LONG.equals(in.getSide()) ? Type.LONG : Type.SHORT)
        .size(in.getQuantity())
        .price(in.getAveragePrice())
        .liquidationPrice(in.getEstimatedLiquidationPrice())
        .unRealisedPnl(in.getUnrealisedPnlLastPrice()).build();
  }
}
