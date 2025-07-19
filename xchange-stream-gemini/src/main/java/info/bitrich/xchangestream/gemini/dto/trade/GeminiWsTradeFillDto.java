package info.bitrich.xchangestream.gemini.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeminiWsTradeFillDto {

  @JsonProperty("trade_id")
  private String tradeId;

  @JsonProperty("liquidity")
  private String liquidity;

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("amount")
  private BigDecimal amount;

  @JsonProperty("fee")
  private BigDecimal fee;

  @JsonProperty("fee_currency")
  private String feeCurrency;

}
