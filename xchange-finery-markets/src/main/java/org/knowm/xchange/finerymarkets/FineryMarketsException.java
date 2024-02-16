package org.knowm.xchange.finerymarkets;

import com.fasterxml.jackson.annotation.JsonProperty;
import si.mazi.rescu.HttpStatusExceptionSupport;

public class FineryMarketsException extends HttpStatusExceptionSupport {

  public FineryMarketsException(@JsonProperty("error") String message) {
    super(message);
  }
}
