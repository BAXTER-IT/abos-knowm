package info.bitrich.xchangestream.thalex.dto;

import static info.bitrich.xchangestream.thalex.Constants.METHOD_PING;

public class ThalexWsPingMessage extends ThalexWsMessage {

  public ThalexWsPingMessage() {
    method = METHOD_PING;
  }
}
