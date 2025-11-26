package info.bitrich.xchangestream.bybit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class BybitWsResponseDto<T> {

  @JsonProperty("id")
  private String id;

  @JsonProperty("topic")
  private String topic;

  @JsonProperty("creationTime")
  private Date creationTime;

  @JsonProperty("data")
  private List<T> data;
}
