package org.knowm.xchange.gemini.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeminiException extends RuntimeException {

  @JsonProperty("message")
  private String message;

  public GeminiException(@JsonProperty("message") String message) {

    super();
    this.message = message;
  }

  public String getMessage() {

    return message;
  }
}
