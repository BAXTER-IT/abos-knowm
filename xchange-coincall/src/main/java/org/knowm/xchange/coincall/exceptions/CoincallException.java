package org.knowm.xchange.coincall.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class CoincallException extends RuntimeException {

  private Integer code;

  @JsonProperty("msg")
  private String message;

}
