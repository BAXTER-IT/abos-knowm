package org.knowm.xchange.finerymarkets.streaming.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.knowm.xchange.finerymarkets.streaming.enums.Event;
import org.knowm.xchange.finerymarkets.streaming.enums.Feed;

@Getter
@AllArgsConstructor
public class FineryMarketsStreamingRequestBaseDto {

  private final Event event;
  private final Feed feed;

}
