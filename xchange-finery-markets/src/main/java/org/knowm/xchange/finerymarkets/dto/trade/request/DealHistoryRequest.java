package org.knowm.xchange.finerymarkets.dto.trade.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.knowm.xchange.finerymarkets.dto.FineryMarketsRequest;
import org.knowm.xchange.finerymarkets.dto.trade.DealFilter;

@Getter
@Setter
@Builder
public class DealHistoryRequest implements FineryMarketsRequest {

  /** Instrument name. If specified only return deals for this instrument */
  private String instrument;

  /**
   * Optional field for master accounts only (FM Liquidity Match).
   *
   * <ul>
   *   <li>absent or "all": returns full deals history
   *   <li>"subaccounts": returns deals history with subaccounts
   *   <li>"external": returns deals history with external counterparties
   * </ul>
   */
  private DealFilter filter;

  /** If specified only return deals with lesser ID */
  private Long till;

  /** If specified only return deals with equal or greater timestamp */
  private Long from;

  /** If specified only return deals with lesser timestamp */
  private Long to;

  /** Default: 250. Maximum number of deals to return (capped at 250) */
  private Short limit;
}
