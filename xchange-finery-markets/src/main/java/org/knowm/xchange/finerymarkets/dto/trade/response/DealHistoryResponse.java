package org.knowm.xchange.finerymarkets.dto.trade.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.knowm.xchange.finerymarkets.dto.trade.DealHistory;
import org.knowm.xchange.finerymarkets.utils.DealHistoryResponseDeserializer;

@Getter
@Setter
@ToString
@JsonDeserialize(using = DealHistoryResponseDeserializer.class)
public class DealHistoryResponse {

  public static final int MAX_DEAL_HISTORY_SIZE = 250;

  private List<DealHistory> deals;

  public DealHistoryResponse() {
    deals = new ArrayList<>();
  }

  public boolean isFullPage() {
    return deals.size() == getMaxDealHistorySize();
  }

  /**
   * If the number of deals in the specified period may exceed the limit, the request returns 250
   * latest deals. To get other deals, a user needs to remember the earliest DealId and specify it
   * in the next request(s).
   * @return the deal id of the earliest deal in the response
   */
  public Long getTillDealId() {
    int dealSize = deals.size();
    if (dealSize == 0) {
      return null;
    }
    return deals.get(dealSize - 1).getDealId();
  }

  protected int getMaxDealHistorySize() {
    return MAX_DEAL_HISTORY_SIZE;
  }
}
