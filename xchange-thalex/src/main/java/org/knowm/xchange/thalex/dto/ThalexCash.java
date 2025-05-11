package org.knowm.xchange.thalex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.currency.Currency;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class ThalexCash {

  /**
   * Currency name.
   */
  @JsonProperty("currency")
  private final Currency currency;

  /**
   * Current balance in this currency.
   */
  @JsonProperty("balance")
  private final BigDecimal balance;

  /**
   * The collateral quality of the asset i.e. the fraction of the asset that can be used as a
   * collateral.
   */
  @JsonProperty("collateral_factor")
  private final BigDecimal collateralFactor;

  /**
   * Index price used to calculate collateral effect of this position. Can be null for assets that
   * are not converted using an index, e.g. for stable coins.
   */
  @JsonProperty("collateral_index_price")
  private final BigDecimal collateralIndexPrice;

  /**
   * If this flag is true, this currency can be deposited and withdrawn.
   */
  @JsonProperty("transactable")
  private final Boolean transactable;
}
