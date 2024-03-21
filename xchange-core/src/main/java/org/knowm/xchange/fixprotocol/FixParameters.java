package org.knowm.xchange.fixprotocol;

import java.util.function.Consumer;
import lombok.Builder;
import org.knowm.xchange.dto.trade.UserTrade;

@Builder()
public class FixParameters {

  String apiKey;
  String secretKey;
  Consumer<UserTrade> messageConsumer;

}
