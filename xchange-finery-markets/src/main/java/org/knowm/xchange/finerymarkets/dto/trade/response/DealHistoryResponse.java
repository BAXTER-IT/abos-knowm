package org.knowm.xchange.finerymarkets.dto.trade.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

  public boolean isFullPage() {
    return deals.size() == MAX_DEAL_HISTORY_SIZE;
  }

  public long getTillId() {
    // TODO check if first or last one, or sort needed
    return deals.get(0).getDealId();
  }
}
