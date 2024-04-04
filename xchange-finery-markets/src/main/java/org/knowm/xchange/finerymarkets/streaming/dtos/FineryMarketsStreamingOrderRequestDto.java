package org.knowm.xchange.finerymarkets.streaming.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.knowm.xchange.finerymarkets.streaming.enums.Event;
import org.knowm.xchange.finerymarkets.streaming.enums.Feed;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FineryMarketsStreamingOrderRequestDto extends FineryMarketsStreamingRequestBaseDto {

  private final String feedId;

  public FineryMarketsStreamingOrderRequestDto(Event event) {
    super(event, Feed.ORDERS);
    this.feedId = null;
  }

  public FineryMarketsStreamingOrderRequestDto(String feedId) {
    super(Event.SUBSCRIBE, Feed.ORDERS);
    this.feedId = feedId;
  }

  public FineryMarketsStreamingOrderRequestDto(Event event, String feedId) {
    super(event, Feed.ORDERS);
    this.feedId = feedId;
  }
}
