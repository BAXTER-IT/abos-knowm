package org.knowm.xchange.finerymarkets.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FineryMarketsNetwork {

  /** Network name */
  @JsonProperty(index = 0)
  private String name;

  /** Network description */
  @JsonProperty(index = 1)
  private String description;

  /** Network ID */
  @JsonProperty(index = 2)
  private int id;
}
