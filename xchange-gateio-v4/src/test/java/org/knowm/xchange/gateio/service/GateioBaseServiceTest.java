package org.knowm.xchange.gateio.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.gateio.GateioExchange;
import org.knowm.xchange.gateio.config.Config;
import si.mazi.rescu.BodyLoggingRestInvocationHandler;
import si.mazi.rescu.CustomRestProxyFactoryImpl;

/**
 * Contains the example of overriding of RestProxyFactory for exchange for some specific logic
 */
class GateioBaseServiceTest{
  protected static GateioExchange exchange;

  @BeforeAll
  static void setUp() {
    Config.getInstance().setRestProxyFactoryClass(CustomRestProxyFactoryImpl.class);
    ExchangeSpecification exSpec = new ExchangeSpecification(GateioExchange.class);
    exchange = (GateioExchange) ExchangeFactory.INSTANCE.createExchange(exSpec);
  }

  @Test
  void correct_proxy_factory() {
    GateioBaseService service = ((GateioBaseService) exchange.getAccountService());
    assertThat(Proxy.getInvocationHandler(service.gateio) instanceof BodyLoggingRestInvocationHandler).isTrue();
  }

}