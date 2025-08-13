package org.knowm.xchange.thalex.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ThalexException extends RuntimeException {

  private Integer code;
  private String message;

  @JsonProperty("error")
  private void unpackErrorFields(Map<String, String> error) {
    code = Integer.parseInt(error.get("code"));
    message = error.get("message");
  }
}
