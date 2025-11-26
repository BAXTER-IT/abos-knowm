package info.bitrich.xchangestream.okex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.okex.dto.enums.OkexEventType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OkexStreamDto<T> {

  @JsonProperty("arg")
  private OkexStreamArgDto arguments;

  /**
   * Event type (snapshot or event_update).
   */
  @JsonProperty("eventType")
  private OkexEventType eventType;

  /**
   * Current page number (snapshot only).
   */
  @JsonProperty("curPage")
  private Integer currentPage;

  /**
   * Whether this is the last snapshot page.
   */
  @JsonProperty("lastPage")
  private Boolean lastPage;

  /**
   * Subscribed position data.
   */
  @JsonProperty("data")
  private T data;

  /**
   * Raw JSON message.
   */
  private String rawJson;
}
