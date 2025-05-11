package org.knowm.xchange.thalex.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.derivative.OptionsContract.OptionType;
import org.knowm.xchange.thalex.config.converters.ThalexStringToOptionTypeConverter;
import org.knowm.xchange.thalex.dto.enums.ThalexInstrumentType;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class ThalexInstrumentDto {

  @JsonProperty("instrument_name")
  private final String instrumentName;

  @JsonProperty("product")
  private final String product;

  @JsonProperty("tick_size")
  private final BigDecimal tickSize;

  @JsonProperty("volume_tick_size")
  private final BigDecimal volumeTickSize;

  /**
   * Minimum order amount for this instrument. This value is always greater or equal to
   * volume_tick_size. If this value is greater than volume_tick_size, it is not possible to insert
   * an order of a smaller amount, or amend an existing order to a smaller amount. However, orders
   * on the books can have smaller remaining amounts as they get partially filled, down to the
   * minimum of volume_tick_size.
   */
  @JsonProperty("min_order_amount")
  private final BigDecimal minOrderAmount;

  /**
   * Related index, e.g. "BTCUSD".
   */
  @JsonProperty("underlying")
  private final String underlying;

  @JsonProperty("type")
  private final ThalexInstrumentType type;

  @JsonProperty("option_type")
  @JsonDeserialize(converter = ThalexStringToOptionTypeConverter.class)
  private final OptionType optionType;

  /**
   * Expiration date in ISO format (YYYY-mm-dd).
   */
  @JsonProperty("expiry_date")
  private final String expiryDate;

  /**
   * Expiration time as UNIX timestamp (seconds).
   */
  @JsonProperty("expiration_timestamp")
  private final Instant expirationTimestamp;

  /**
   * Strike price of option.
   */
  @JsonProperty("strike_price")
  private final BigDecimal strikePrice;

  /**
   * Base currency for pricing (i.e. USD).
   */
  @JsonProperty("base_currency")
  private final String baseCurrency;

  /**
   * For combinations, array of objects with instrument_name and quantity.
   */
  @JsonProperty("legs")
  private final List<ThalexInstrumentLegDto> legs;

  /**
   * Creation time (UNIX timestamp).
   */
  @JsonProperty("create_time")
  private final Instant createTime;

  /**
   * For expired instruments, the final settlement price.
   */
  @JsonProperty("settlement_price")
  private final BigDecimal settlementPrice;

  /**
   * For expired instruments, the underlying delivery price.
   */
  @JsonProperty("settlement_index_price")
  private final BigDecimal settlementIndexPrice;
}
