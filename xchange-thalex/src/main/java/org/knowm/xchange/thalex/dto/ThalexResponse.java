package org.knowm.xchange.thalex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ThalexResponse<T> {

  @JsonProperty("id")
  private String id;

  @JsonProperty(value = "result")
  private T result;
}
