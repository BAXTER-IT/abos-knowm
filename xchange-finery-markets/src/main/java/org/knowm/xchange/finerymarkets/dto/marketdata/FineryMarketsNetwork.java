package org.knowm.xchange.finerymarkets.dto.marketdata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.knowm.xchange.finerymarkets.utils.FineryMarketsNetworkResponseDeserializer;

@Getter
@Setter
@Builder
@JsonDeserialize(using = FineryMarketsNetworkResponseDeserializer.class)
public class FineryMarketsNetwork {

  /** Network name */
  private String name;

  /** Network description */
  private String description;

  /** Network ID */
  private int id;

  private String rawJson;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    FineryMarketsNetwork that = (FineryMarketsNetwork) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
