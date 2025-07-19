package org.knowm.xchange.gemini.dto;

import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class GeminiPostRequest {

  @Default
  public Double nonce = System.currentTimeMillis() / 1000.0;

  public String account;

  public abstract String getRequest();

}
