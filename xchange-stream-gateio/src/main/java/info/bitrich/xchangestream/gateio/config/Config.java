package info.bitrich.xchangestream.gateio.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import lombok.Data;

@Data
public final class Config {

  public static final String WS_URL_V4_SPOT = "wss://api.gateio.ws/ws/v4/";
  public static final String WS_URL_V4_PERPETUAL_FUTURES_BTC = "wss://fx-ws.gateio.ws/v4/ws/btc";
  public static final String WS_URL_V4_PERPETUAL_FUTURES_USDT = "wss://fx-ws.gateio.ws/v4/ws/usdt";
  public static final String WS_URL_V4_DELIVERY_FUTURES_BTC = "wss://fx-ws.gateio.ws/v4/ws/delivery/btc";
  public static final String WS_URL_V4_DELIVERY_FUTURES_USDT = "wss://fx-ws.gateio.ws/v4/ws/delivery/usdt";

  public static final String PRODUCT_SPOT = "spot";
  public static final String PRODUCT_PERPETUAL_FUTURES = "futures.perpetual";
  public static final String PRODUCT_DELIVERY_FUTURES = "futures.delivery";

  public static final String CHANNEL_SPOT_ORDER_BOOK = "spot.order_book";
  public static final String CHANNEL_SPOT_TRADES = "spot.trades";
  public static final String CHANNEL_SPOT_TICKERS = "spot.tickers";
  public static final String CHANNEL_SPOT_BALANCES = "spot.balances";
  public static final String CHANNEL_SPOT_USER_TRADES = "spot.usertrades";
  public static final String CHANNEL_FUTURES_PING = "futures.ping";
  public static final String CHANNEL_FUTURES_PONG = "futures.pong";
  public static final String CHANNEL_FUTURES_POSITIONS = "futures.positions";
  public static final String CHANNEL_FUTURES_BALANCES = "futures.balances";

  public static final List<String> PRIVATE_CHANNELS = Arrays.asList(CHANNEL_SPOT_BALANCES,
      CHANNEL_SPOT_USER_TRADES, CHANNEL_FUTURES_POSITIONS, CHANNEL_FUTURES_BALANCES);

  public static final String CHANNEL_NAME_DELIMITER = "-";
  private static Config instance = new Config();
  private ObjectMapper objectMapper;
  private Clock clock;

  private Config() {
    clock = Clock.systemDefaultZone();

    objectMapper = new ObjectMapper();

    // by default read and write timetamps as milliseconds
    objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

    // don't fail un unknown properties
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // don't write nulls
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    // enable parsing to Instant
    objectMapper.registerModule(new JavaTimeModule());
  }

  public static Config getInstance() {
    return instance;
  }
}
