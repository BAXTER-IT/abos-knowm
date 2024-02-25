package org.knowm.xchange.finerymarkets.dto.marketdata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.knowm.xchange.finerymarkets.utils.FineryMarketsInstrumentResponseDeserializer;

@Getter
@Setter
@Builder
@JsonDeserialize(using = FineryMarketsInstrumentResponseDeserializer.class)
public class FineryMarketsInstrument {

  /** Instrument Name */
  private String name;

  /** Instrument Id (used only in WebSocket Feed B as feedId) */
  private long id;

  /** Asset Currency Name */
  private String assetCurrencyName;

  /** Balance Currency Name */
  private String balanceCurrencyName;

  private String rawData;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FineryMarketsInstrument that = (FineryMarketsInstrument) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
