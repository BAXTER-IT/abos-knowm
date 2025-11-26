package info.bitrich.xchangestream.gateio.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import info.bitrich.xchangestream.gateio.config.Config;
import info.bitrich.xchangestream.gateio.dto.Event;
import info.bitrich.xchangestream.gateio.dto.response.balance.GateioFuturesBalancesNotification;
import info.bitrich.xchangestream.gateio.dto.response.balance.GateioSpotBalancesNotification;
import info.bitrich.xchangestream.gateio.dto.response.orderbook.GateioOrderBookNotification;
import info.bitrich.xchangestream.gateio.dto.response.ticker.GateioTickerNotification;
import info.bitrich.xchangestream.gateio.dto.response.trade.GateioTradeNotification;
import info.bitrich.xchangestream.gateio.dto.response.usertrade.GateioMultipleUserTradeNotification;
import java.time.Instant;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.gateio.config.converter.TimestampSecondsToInstantConverter;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "channel",
    visible = true)
@JsonSubTypes({
    @Type(value = GateioTradeNotification.class, name = Config.CHANNEL_SPOT_TRADES),
    @Type(value = GateioTickerNotification.class, name = Config.CHANNEL_SPOT_TICKERS),
    @Type(value = GateioOrderBookNotification.class, name = Config.CHANNEL_SPOT_ORDER_BOOK),
    @Type(value = GateioMultipleUserTradeNotification.class, name = Config.CHANNEL_SPOT_USER_TRADES),
    @Type(value = GateioFuturePositionsNotification.class, name = Config.CHANNEL_FUTURES_POSITIONS),
    @Type(value = GateioFuturesBalancesNotification.class, name = Config.CHANNEL_FUTURES_BALANCES),
    @Type(value = GateioSpotBalancesNotification.class, name = Config.CHANNEL_SPOT_BALANCES),
})
@Data
@SuperBuilder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class GateioWsNotification {

  @JsonProperty("time")
  @JsonDeserialize(converter = TimestampSecondsToInstantConverter.class)
  private Instant time;

  @JsonProperty("time_ms")
  private Instant timeMs;

  @JsonProperty("channel")
  private String channel;

  @JsonProperty("event")
  private Event event;

  @JsonProperty("error")
  private String error;

  public String getUniqueChannelName() {
    return channel;
  }

}
