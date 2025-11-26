package info.bitrich.xchangestream.bitget.dto.common;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BitgetChannelWithCoin extends BitgetChannel {

  @JsonProperty("coin")
  private String coin;
}
