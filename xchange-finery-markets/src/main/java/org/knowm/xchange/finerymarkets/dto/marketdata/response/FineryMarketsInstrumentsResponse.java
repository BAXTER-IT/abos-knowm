package org.knowm.xchange.finerymarkets.dto.marketdata.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsCurrency;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsInstrument;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsNetwork;
import org.knowm.xchange.finerymarkets.utils.FineryMarketsInstrumentsResponseDeserializer;

@Getter
@Setter
@JsonDeserialize(using = FineryMarketsInstrumentsResponseDeserializer.class)
public class FineryMarketsInstrumentsResponse {

  private List<FineryMarketsCurrency> currencies;
  private List<FineryMarketsInstrument> instruments;
  private List<FineryMarketsNetwork> networks;
}
