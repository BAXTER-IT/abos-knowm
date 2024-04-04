package org.knowm.xchange.finerymarkets.streaming.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.knowm.xchange.finerymarkets.dto.trade.DealHistory;
import org.knowm.xchange.finerymarkets.streaming.enums.Action;
import org.knowm.xchange.finerymarkets.streaming.enums.Feed;
import org.knowm.xchange.finerymarkets.utils.FineryMarketsStreamingOrderResponseDeserializer;

@Getter
@Setter
@ToString
@Builder
@JsonDeserialize(using = FineryMarketsStreamingOrderResponseDeserializer.class)
public class FineryMarketsStreamingOrderResponseDto {

  private Feed feed;
  private String feedId;
  private Action action;
  private List<DealHistory> deals;
}
