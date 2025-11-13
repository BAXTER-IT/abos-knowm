package org.knowm.xchange.coincall.dtos.marketdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoincallFuturesInstrumentDto {

  /**
   * Symbol ID, e.g. BTC-USD
   */
  @JsonProperty("ticker_id")
  private final String tickerId;

  @JsonProperty("base_currency")
  private final String baseCurrency;

  @JsonProperty("quote_currency")
  private final String quoteCurrency;

  @JsonProperty("last_price")
  private final BigDecimal lastPrice;

  /**
   * 24h volume in BASE currency
   */
  @JsonProperty("base_volume")
  private final BigDecimal baseVolume;

  /**
   * 24h USD volume
   */
  @JsonProperty("usd_volume")
  private final BigDecimal usdVolume;

  /**
   * 24h QUOTE currency volume
   */
  @JsonProperty("quote_volume")
  private final BigDecimal quoteVolume;

  /**
   * Best bid price
   */
  @JsonProperty("bid")
  private final BigDecimal bid;

  /**
   * Best ask price
   */
  @JsonProperty("ask")
  private final BigDecimal ask;

  /**
   * 24h high
   */
  @JsonProperty("high")
  private final BigDecimal high;

  /**
   * 24h low
   */
  @JsonProperty("low")
  private final BigDecimal low;

  @JsonProperty("product_type")
  private final String productType;

  @JsonProperty("open_interest")
  private final BigDecimal openInterest;

  @JsonProperty("open_interest_usd")
  private final BigDecimal openInterestUsd;

  /**
   * Index price for contract
   */
  @JsonProperty("index_price")
  private final BigDecimal indexPrice;

  /**
   * Current funding rate
   */
  @JsonProperty("funding_rate")
  private final BigDecimal fundingRate;

  /**
   * Next funding rate
   */
  @JsonProperty("next_funding_rate")
  private final BigDecimal nextFundingRate;

  /**
   * Next funding timestamp (ms)
   */
  @JsonProperty("next_funding_rate_timestamp")
  private final long nextFundingRateTimestamp;

  @JsonProperty("contract_type")
  private final String contractType;

  /**
   * Contract mark/last price
   */
  @JsonProperty("contract_price")
  private final BigDecimal contractPrice;

  @JsonProperty("contract_price_currency")
  private final String contractPriceCurrency;
}