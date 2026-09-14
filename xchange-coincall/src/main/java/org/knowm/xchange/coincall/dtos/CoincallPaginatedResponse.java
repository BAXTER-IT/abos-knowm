package org.knowm.xchange.coincall.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Data;
import org.knowm.xchange.utils.jackson.RawJsonDataDeserializer;

@Data
public class CoincallPaginatedResponse<T> {

  @JsonProperty("list")
  @JsonDeserialize(using = RawJsonDataDeserializer.class)
  private List<T> list;

  @JsonProperty("hasNext")
  private boolean hasNext;

  @JsonProperty("hasPre")
  private boolean hasPre;
}