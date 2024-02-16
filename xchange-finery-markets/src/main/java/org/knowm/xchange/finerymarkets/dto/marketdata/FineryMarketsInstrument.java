package org.knowm.xchange.finerymarkets.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FineryMarketsInstrument {

  /** Instrument Name */
  @JsonProperty(index = 0)
  private String name;

  /** Instrument Id (used only in WebSocket Feed B as feedId) */
  @JsonProperty(index = 1)
  private int id;

  /** Asset Currency Name */
  @JsonProperty(index = 2)
  private String assetCurrencyName;

  /** Balance Currency Name */
  @JsonProperty(index = 3)
  private String balanceCurrencyName;

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
