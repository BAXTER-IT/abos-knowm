package org.knowm.xchange.coincall.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.coincall.CoincallAdapters;
import org.knowm.xchange.coincall.CoincallExchange;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallFuturesInstrumentDto;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallSpotInstrumentDto;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class CoincallMarketDataService extends CoincallMarketDataServiceRaw implements
    MarketDataService {

  public CoincallMarketDataService(CoincallExchange exchange) {
    super(exchange);
  }

  @Override
  public Map<Instrument, InstrumentMetaData> getInstruments() throws IOException {
    List<CoincallSpotInstrumentDto> spotInstruments = getCoincallSpotInstruments();
    List<CoincallFuturesInstrumentDto> futuresInstruments = getCoincallFuturesInstruments();
    Map<Instrument, InstrumentMetaData> instrumentsMap = CoincallAdapters.toInstrumentsMap(
        spotInstruments);
    instrumentsMap.putAll(CoincallAdapters.toInstumentsMap(futuresInstruments));
    return instrumentsMap;
  }

}
