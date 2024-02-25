package org.knowm.xchange.finerymarkets.dto.marketdata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.knowm.xchange.finerymarkets.utils.FineryMarketsCurrencyResponseDeserializer;

@Getter
@Setter
@Builder
@JsonDeserialize(using = FineryMarketsCurrencyResponseDeserializer.class)
public class FineryMarketsCurrency {

  /** Currency name */
  private String name;

  /** Currency Id */
  private int id;

  /** Balance Step, minimum fraction of currency. From 1 to 1000000 */
  private int balanceStep;

  /** Current USD valuation for risk control */
  private long price;

  /** Currency Type Name */
  private String typeName;

  /**
   * Array of network names available for that currency (Field is not present for feed 'I' snapshot)
   */
  private List<String> networks;

  private String rawData;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FineryMarketsCurrency that = (FineryMarketsCurrency) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
