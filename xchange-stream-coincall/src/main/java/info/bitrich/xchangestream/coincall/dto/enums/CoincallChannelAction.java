package info.bitrich.xchangestream.coincall.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoincallChannelAction {
  SUBSCRIBE("subscribe"),
  UNSUBSCRIBE("unSubscribe"),
  HEARTBEAT("heartbeat");

  @JsonValue
  private final String action;
}
