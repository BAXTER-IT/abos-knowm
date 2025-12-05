package org.knowm.xchange.coincall.services;

import java.io.IOException;
import java.util.List;
import org.knowm.xchange.coincall.CoincallExchange;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallFuturesInstrumentDto;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallSpotInstrumentDto;

public class CoincallMarketDataServiceRaw extends CoincallBaseService {

  public CoincallMarketDataServiceRaw(CoincallExchange exchange) {
    super(exchange);
  }

  List<CoincallSpotInstrumentDto> getCoincallSpotInstruments() throws IOException {
    return coincall.spotInstruments().getData();
  }

  List<CoincallFuturesInstrumentDto> getCoincallFuturesInstruments() throws IOException {
    return coincall.futuresInstruments().getData();
  }
}
