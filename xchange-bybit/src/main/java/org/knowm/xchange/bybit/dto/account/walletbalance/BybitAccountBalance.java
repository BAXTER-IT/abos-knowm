package org.knowm.xchange.bybit.dto.account.walletbalance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class BybitAccountBalance {

  @JsonProperty("accountType")
  BybitAccountType accountType;

  @JsonProperty("accountIMRate")
  String accountIMRate;

  @JsonProperty("accountMMRate")
  String accountMMRate;

  /** Same calculation as accountIMRate (legacy but not marked deprecated). */
  @JsonProperty("accountIMRateByMp")
  String accountIMRateByMp;

  /** Same calculation as accountMMRate (legacy but not marked deprecated). */
  @JsonProperty("accountMMRateByMp")
  String accountMMRateByMp;

  @JsonProperty("totalEquity")
  String totalEquity;

  @JsonProperty("totalWalletBalance")
  String totalWalletBalance;

  @JsonProperty("totalMarginBalance")
  String totalMarginBalance;

  @JsonProperty("totalAvailableBalance")
  String totalAvailableBalance;

  @JsonProperty("totalPerpUPL")
  String totalPerpUPL;

  @JsonProperty("totalInitialMargin")
  String totalInitialMargin;

  @JsonProperty("totalMaintenanceMargin")
  String totalMaintenanceMargin;

  @JsonProperty("totalInitialMarginByMp")
  String totalInitialMarginByMp;

  @JsonProperty("totalMaintenanceMarginByMp")
  String totalMaintenanceMarginByMp;

  /** Per-coin wallet records. */
  @JsonProperty("coin")
  List<BybitCoinWalletBalance> coins;
}