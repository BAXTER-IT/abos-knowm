package org.knowm.xchange.finerymarkets;

import java.io.IOException;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.finerymarkets.service.FineryMarketsAccountService;
import org.knowm.xchange.finerymarkets.service.FineryMarketsMarketDataService;
import org.knowm.xchange.finerymarkets.service.FineryMarketsTradeService;

public class FineryMarketsExchange extends BaseExchange {

  @Override
  protected void initServices() {
    marketDataService = new FineryMarketsMarketDataService(this);
    tradeService = new FineryMarketsTradeService(this);
    accountService = new FineryMarketsAccountService(this);
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {
    ExchangeSpecification exchangeSpecification = new ExchangeSpecification(this.getClass());
    exchangeSpecification.setSslUri("https://trade.finerymarkets.com");
    exchangeSpecification.setHost("trade.finerymarkets.com");
    exchangeSpecification.setPort(80);
    exchangeSpecification.setExchangeName("FineryMarkets");
    exchangeSpecification.setExchangeDescription(
        "Finery Markets is the first global crypto-native Multi-Dealer Platform. Serving clients since 2019.");
    exchangeSpecification.setShouldLoadRemoteMetaData(false);
    exchangeSpecification.setExchangeSpecificParametersItem(USE_SANDBOX, false);
    return exchangeSpecification;
  }

  @Override
  public void applySpecification(ExchangeSpecification exchangeSpecification) {
    if (useSandbox(exchangeSpecification)) {
      exchangeSpecification.setSslUri("https://test.finerymarkets.com");
    }
    super.applySpecification(exchangeSpecification);
  }

  @Override
  public void remoteInit() throws IOException, ExchangeException {
    // initialize currency pairs & currencies
    exchangeMetaData =
        new ExchangeMetaData(
            marketDataService.getInstruments(),
            marketDataService.getCurrencies(),
            null,
            null,
            true);
  }

  protected boolean useSandbox(ExchangeSpecification exchangeSpecification) {
    return Boolean.TRUE.equals(
        exchangeSpecification.getExchangeSpecificParametersItem(USE_SANDBOX));
  }
}
