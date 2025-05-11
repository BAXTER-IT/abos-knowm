package org.knowm.xchange.thalex;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.thalex.services.ThalexAccountService;
import org.knowm.xchange.thalex.services.ThalexMarketDataService;
import org.knowm.xchange.thalex.services.ThalexMarketDataServiceRaw;
import org.knowm.xchange.thalex.services.ThalexTradeService;

@Slf4j
public class ThalexExchange extends BaseExchange {

  @Override
  protected void initServices() {
    accountService = new ThalexAccountService(this);
    marketDataService = new ThalexMarketDataService(this);
    tradeService = new ThalexTradeService(this);

    populateInstrumentMap();
  }

  public void populateInstrumentMap() {
    // initialize instrument mappings
    ThalexMarketDataServiceRaw marketDataServiceRaw =
        (ThalexMarketDataServiceRaw) marketDataService;
    try {
      marketDataServiceRaw.getThalexInstrument().forEach(ThalexAdapters::addInstrumentToMap);
    } catch (Exception e) {
      log.warn("Error fetching instruments from Thalex: {}", e.getMessage());
    }
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {
    ExchangeSpecification specification = new ExchangeSpecification(getClass());
    specification.setSslUri("https://thalex.com");
    specification.setHost("www.thalex.com");
    specification.setWsEndpoint("wss://thalex.com/ws/api/v2");
    specification.setExchangeName("Thalex");
    specification.setShouldLoadRemoteMetaData(false);
    return specification;
  }

  @Override
  public void applySpecification(ExchangeSpecification exchangeSpecification) {
    if (useSandbox(exchangeSpecification)) {
      exchangeSpecification.setSslUri("https://testnet.thalex.com");
      exchangeSpecification.setHost("testnet.thalex.com");
      exchangeSpecification.setWsEndpoint("wss://testnet.thalex.com/ws/api/v2");
    }
    super.applySpecification(exchangeSpecification);
  }

  protected boolean useSandbox(ExchangeSpecification exchangeSpecification) {
    return Boolean.TRUE.equals(
        exchangeSpecification.getExchangeSpecificParametersItem(USE_SANDBOX));
  }
}
