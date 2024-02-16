package org.knowm.xchange.finerymarkets.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FineryMarketsCurrency {

  /** Currency name */
  @JsonProperty(index = 0)
  private String name;

  /** Currency Id */
  @JsonProperty(index = 1)
  private int id;

  /** Balance Step, minimum fraction of currency. From 1 to 1000000 */
  @JsonProperty(index = 2)
  private int balanceSize;

  /** Current USD valuation for risk control */
  @JsonProperty(index = 3)
  private long price;

  /** Currency Type Name */
  @JsonProperty(index = 4)
  private String typeName;

  /**
   * Array of network names available for that currency (Field is not present for feed 'I' snapshot)
   */
  @JsonProperty(index = 5)
  private List<String> networks;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    FineryMarketsCurrency that = (FineryMarketsCurrency) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
