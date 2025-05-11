package org.knowm.xchange.thalex.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.thalex.ThalexAdapters;
import org.knowm.xchange.thalex.ThalexExchange;
import org.knowm.xchange.thalex.dto.marketdata.ThalexInstrumentDto;

public class ThalexMarketDataService extends ThalexMarketDataServiceRaw
    implements MarketDataService {

  public ThalexMarketDataService(ThalexExchange exchange) {
    super(exchange);
  }

  @Override
  public Map<Instrument, InstrumentMetaData> getInstruments() throws IOException {
    List<ThalexInstrumentDto> thalexInstruments = getThalexInstrument();
    return ThalexAdapters.toInstrumentsMap(thalexInstruments);
  }
}
