package info.bitrich.xchangestream.thalex.dto;

import static info.bitrich.xchangestream.thalex.Constants.METHOD_UNSUBSCRIBE;

import java.util.Collections;

public class ThalexWsUnsubscribeMessage extends ThalexWsMessage {

  public ThalexWsUnsubscribeMessage(String channel) {
    method = METHOD_UNSUBSCRIBE;
    params.put("channels", Collections.singletonList(channel));
  }
}
