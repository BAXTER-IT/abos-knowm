package info.bitrich.xchangestream.thalex;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

  public static final String METHOD_LOGIN = "public/login";
  public static final String METHOD_SUBSCRIBE = "private/subscribe";
  public static final String METHOD_UNSUBSCRIBE = "unsubscribe";
  public static final String METHOD_PING = "public/ping";
  public static final String CHANNEL_TRADE_HISTORY = "account.trade_history";

}
