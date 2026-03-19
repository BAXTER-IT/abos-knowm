package org.knowm.xchange.kucoin.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class FundingHistoryResponse {
  List<FundingEntry> dataList;
  boolean hasMore;
}
