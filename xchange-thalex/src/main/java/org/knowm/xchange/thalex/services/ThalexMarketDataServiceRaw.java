package org.knowm.xchange.thalex.services;

import java.io.IOException;
import java.util.List;
import org.knowm.xchange.thalex.ThalexExchange;
import org.knowm.xchange.thalex.dto.marketdata.ThalexInstrumentDto;

public class ThalexMarketDataServiceRaw extends ThalexBaseService {

  public ThalexMarketDataServiceRaw(ThalexExchange exchange) {
    super(exchange);
  }

  public List<ThalexInstrumentDto> getThalexInstrument() throws IOException {
    return thalex.instruments().getResult();
  }
}
