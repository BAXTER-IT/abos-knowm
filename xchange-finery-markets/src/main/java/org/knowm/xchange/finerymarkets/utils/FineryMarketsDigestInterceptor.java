package org.knowm.xchange.finerymarkets.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.finerymarkets.dto.DecoratedPayload;
import si.mazi.rescu.Interceptor;

@Slf4j
public class FineryMarketsDigestInterceptor implements Interceptor {

  @Override
  public Object aroundInvoke(
      InvocationHandler invocationHandler, Object o, Method method, Object[] objects)
      throws Throwable {

    for (Object object : objects) {
      if (object.getClass() == DecoratedPayload.class) {
        DecoratedPayload payload = (DecoratedPayload) object;
        payload.setNonceAndTimestamp();
      }
    }

    return invocationHandler.invoke(o, method, objects);
  }
}
