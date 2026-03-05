package info.bitrich.xchangestream.gateio.dto.response.balance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class GateioFuturesBalancePayload {

  /** Balance after the change. */
  @JsonProperty("balance")
  BigDecimal balance;

  /** Difference since last event. */
  @JsonProperty("change")
  BigDecimal change;

  /** Optional message text. */
  @JsonProperty("text")
  String text;

  /** Timestamp in seconds (string or integer in raw feed). */
  @JsonProperty("time")
  Instant time;

  /** Timestamp in milliseconds. */
  @JsonProperty("time_ms")
  Instant timeMs;

  /** Event type. */
  @JsonProperty("type")
  ChangeType type;

  /** User ID. */
  @JsonProperty("user")
  String user;

  /** Currency (transfer unit). */
  @JsonProperty("currency")
  String currency;
}
