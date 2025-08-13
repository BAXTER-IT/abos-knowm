package org.knowm.xchange.thalex.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ThalexTradesDto {

  @JsonProperty(value = "trades")
  private List<ThalexTradeDto> trades;

  @JsonProperty("bookmark")
  private String bookmark;
}
