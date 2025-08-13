package info.bitrich.xchangestream.thalex.dto;

import static info.bitrich.xchangestream.thalex.Constants.METHOD_SUBSCRIBE;

import java.util.Collections;

public class ThalexWsSubscribeMessage extends ThalexWsMessage {

  public ThalexWsSubscribeMessage(String channel) {
    method = METHOD_SUBSCRIBE;
    params.put("channels", Collections.singletonList(channel));
  }
}
