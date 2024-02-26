package org.knowm.xchange.finerymarkets;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nullable;
import si.mazi.rescu.HttpStatusExceptionSupport;
import si.mazi.rescu.InvocationAware;
import si.mazi.rescu.RestInvocation;

public class FineryMarketsException extends HttpStatusExceptionSupport implements InvocationAware {

  private RestInvocation invocation;

  public FineryMarketsException(@JsonProperty("error") String message) {
    super(message);
  }

  @Nullable
  @Override
  public RestInvocation getInvocation() {
    return invocation;
  }

  @Override
  public void setInvocation(@Nullable RestInvocation restInvocation) {
    this.invocation = restInvocation;
  }
}
