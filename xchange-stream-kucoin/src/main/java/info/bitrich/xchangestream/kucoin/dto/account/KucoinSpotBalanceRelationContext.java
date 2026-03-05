package info.bitrich.xchangestream.kucoin.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class KucoinSpotBalanceRelationContext {

  /**
   * Symbol related to this balance change, e.g. BTC-USDT.
   */
  @JsonProperty("symbol")
  private String symbol;

  /**
   * Related order ID.
   */
  @JsonProperty("orderId")
  private String orderId;

  /**
   * Related trade ID.
   */
  @JsonProperty("tradeId")
  private String tradeId;
}
