package info.bitrich.xchangestream.thalex.dto;

import static info.bitrich.xchangestream.thalex.Constants.METHOD_LOGIN;

public class ThalexWsLoginMessage extends ThalexWsMessage {

  public ThalexWsLoginMessage(String jwt) {
    method = METHOD_LOGIN;
    params.put("token", jwt);
  }

}
