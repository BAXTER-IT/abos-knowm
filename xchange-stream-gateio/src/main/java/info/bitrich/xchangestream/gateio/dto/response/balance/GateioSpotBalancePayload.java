package info.bitrich.xchangestream.gateio.dto.response.balance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.gateio.config.converter.TimestampSecondsToInstantConverter;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class GateioSpotBalancePayload {

  /**
   * Unix timestamp in seconds.
   */
  @JsonProperty("timestamp")
  @JsonDeserialize(converter = TimestampSecondsToInstantConverter.class)
  Instant timestamp;

  /**
   * Unix timestamp in milliseconds.
   */
  @JsonProperty("timestamp_ms")
  Instant timestampMs;

  /**
   * User ID.
   */
  @JsonProperty("user")
  String user;

  /**
   * Currency affected.
   */
  @JsonProperty("currency")
  Currency currency;

  /**
   * Amount changed.
   */
  @JsonProperty("change")
  BigDecimal change;

  /**
   * Total spot balance after the update.
   */
  @JsonProperty("total")
  BigDecimal total;

  /**
   * Balance available to use.
   */
  @JsonProperty("available")
  BigDecimal available;

  /**
   * Locked balance amount.
   */
  @JsonProperty("freeze")
  BigDecimal freeze;

  /**
   * Locked balance change amount.
   */
  @JsonProperty("freeze_change")
  BigDecimal freezeChange;

  /**
   * Balance change reason.
   */
  @JsonProperty("change_type")
  ChangeType changeType;

}
