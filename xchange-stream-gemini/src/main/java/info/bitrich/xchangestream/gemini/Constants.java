package info.bitrich.xchangestream.gemini;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

  public static final String HEADER_APIKEY = "X-GEMINI-APIKEY";
  public static final String HEADER_PAYLOAD = "X-GEMINI-PAYLOAD";
  public static final String HEADER_SIGNATURE = "X-GEMINI-SIGNATURE";
  public static final String ORDER_EVENT_TYPE_SUBSCRIPTION_ACK = "subscription_ack";
  public static final String ORDER_EVENT_TYPE_HEARTBEAT = "heartbeat";
  public static final String CHANNEL_TRADE_HISTORY = "/v1/order/events";
}
