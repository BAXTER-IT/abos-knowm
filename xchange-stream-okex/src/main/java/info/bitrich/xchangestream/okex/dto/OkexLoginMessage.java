package info.bitrich.xchangestream.okex.dto;

import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class OkexLoginMessage {

  List<LoginArg> args = new LinkedList<>();
  private String op = "login";

  @Data
  @AllArgsConstructor
  public static class LoginArg {

    private String apiKey;
    private String passphrase;
    // Unix Epoch time, the unit is seconds
    private String timestamp;
    // https://www.okx.com/docs-v5/en/#websocket-api-login
    private String sign;
  }
}
