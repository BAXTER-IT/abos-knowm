package org.knowm.xchange.finerymarkets.streaming.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.knowm.xchange.finerymarkets.streaming.enums.Event;
import org.knowm.xchange.finerymarkets.streaming.enums.Feed;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FineryMarketsStreamingPingDto extends FineryMarketsStreamingRequestBaseDto {

  public FineryMarketsStreamingPingDto() {
    super(Event.PING, null);
  }
}
