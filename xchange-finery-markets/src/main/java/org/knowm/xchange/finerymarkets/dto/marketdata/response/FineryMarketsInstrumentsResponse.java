package org.knowm.xchange.finerymarkets.dto.marketdata.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsCurrency;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsInstrument;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsNetwork;

@Getter
@Setter
public class FineryMarketsInstrumentsResponse {

  @JsonProperty(index = 0, required = true)
  List<FineryMarketsCurrency> currencies;

  @JsonProperty(index = 1, required = true)
  List<FineryMarketsInstrument> instruments;

  @JsonProperty(index = 2)
  List<FineryMarketsNetwork> networks;
}
