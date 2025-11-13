package org.knowm.xchange.coincall;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coincall.services.CoincallAccountService;
import org.knowm.xchange.coincall.services.CoincallMarketDataService;
import org.knowm.xchange.coincall.services.CoincallTradeService;

@Slf4j
public class CoincallExchange extends BaseExchange {

  public static final String WS_OPTIONS_URI = "WS_OPTIONS_URI";
  public static final String WS_FUTURES_URI = "WS_FUTURES_URI";
  public static final String WS_SPOT_PUBLIC_URI = "WS_SPOT_PUBLIC_URI";
  public static final String WS_SPOT_PRIVATE_URI = "WS_SPOT_PRIVATE_URI";

  @Override
  protected void initServices() {
    accountService = new CoincallAccountService(this);
    marketDataService = new CoincallMarketDataService(this);
    tradeService = new CoincallTradeService(this);
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {
    ExchangeSpecification spec = new ExchangeSpecification(getClass());
    spec.setSslUri("https://api.coincall.com");
    spec.setHost("api.coincall.com");
    spec.setExchangeName("Coincall");

    spec.setWsEndpoint("wss://ws.coincall.com/futures");

    spec.setExchangeSpecificParametersItem(WS_OPTIONS_URI, "wss://ws.coincall.com/options");
    spec.setExchangeSpecificParametersItem(WS_FUTURES_URI, "wss://ws.coincall.com/futures");
    spec.setExchangeSpecificParametersItem(WS_SPOT_PUBLIC_URI, "wss://ws.coincall.com/spot/ws");
    spec.setExchangeSpecificParametersItem(WS_SPOT_PRIVATE_URI,
        "wss://ws.coincall.com/spot/ws/private");

    spec.setShouldLoadRemoteMetaData(false);
    return spec;
  }

}
