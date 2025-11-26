package info.bitrich.xchangestream.kucoin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KucoinWebSocketEvent {

  @JsonProperty("id")
  private String id;

  @JsonProperty("userId")
  private String userId;

  @JsonProperty("type")
  private String type;

  @JsonProperty("topic")
  private String topic;

  @JsonProperty("subject")
  private String subject;

  @JsonProperty("channelType")
  private String channelType;
}
