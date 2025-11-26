package info.bitrich.xchangestream.okex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.okex.dto.enums.OkexInstrumentType;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OkexStreamArgDto {

  /**
   * Channel name.
   */
  @JsonProperty("channel")
  private String channel;

  /**
   * User identifier.
   */
  @JsonProperty("uid")
  private String userId;

  /**
   * Instrument type filter.
   */
  @JsonProperty("instType")
  private OkexInstrumentType instrumentType;

  /**
   * Instrument family, e.g. BTC-USDT.
   */
  @JsonProperty("instFamily")
  private String instrumentFamily;

  /**
   * Instrument ID, e.g. BTC-USDT-SWAP.
   */
  @JsonProperty("instId")
  private String instrumentId;
}
