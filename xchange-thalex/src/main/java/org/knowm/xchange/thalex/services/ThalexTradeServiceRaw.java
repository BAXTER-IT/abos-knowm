package org.knowm.xchange.thalex.services;

import java.io.IOException;
import org.knowm.xchange.thalex.ThalexExchange;
import org.knowm.xchange.thalex.dto.trade.ThalexTradesDto;

public class ThalexTradeServiceRaw extends ThalexBaseService {

  public ThalexTradeServiceRaw(ThalexExchange exchange) {
    super(exchange);
  }

  public ThalexTradesDto getThalexTrades(Long from, Long to, String bookmark) throws IOException {
    return thalexAuthenticated
        .fills(thalexDigest, 1000, from, to, bookmark)
        .getResult();
  }
}
