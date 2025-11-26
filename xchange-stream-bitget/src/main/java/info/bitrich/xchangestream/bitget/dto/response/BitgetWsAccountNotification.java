package info.bitrich.xchangestream.bitget.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.bitget.dto.response.BitgetWsAccountNotification.BitgetAccountBalance;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@SuperBuilder(toBuilder = true)
@Jacksonized
public class BitgetWsAccountNotification extends BitgetWsNotification<BitgetAccountBalance> {

  @Value
  @Builder
  @Jacksonized
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BitgetAccountBalance {

    /** Coin symbol e.g. USDT, BTC. */
    @JsonProperty("coin")
    String coin;

    /** Available balance. */
    @JsonProperty("available")
    BigDecimal available;

    /** Frozen balance (held by open orders). */
    @JsonProperty("frozen")
    BigDecimal frozen;

    /** Locked balance. */
    @JsonProperty("locked")
    BigDecimal locked;

    /** Limit available amount (futures margin constraints). */
    @JsonProperty("limitAvailable")
    BigDecimal limitAvailable;

    /** Update time (Unix ms). */
    @JsonProperty("uTime")
    Long updateTime;
  }

}
