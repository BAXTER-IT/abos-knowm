package org.knowm.xchange.lgo;

import java.io.IOException;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.lgo.dto.LgoException;
import org.knowm.xchange.lgo.dto.currency.LgoCurrencies;
import org.knowm.xchange.lgo.dto.product.LgoProducts;
import org.knowm.xchange.lgo.service.LgoKeyService;
import org.knowm.xchange.lgo.service.LgoMarketDataService;
import org.knowm.xchange.lgo.service.LgoSignatureService;
import org.knowm.xchange.lgo.service.LgoTradeService;
import org.knowm.xchange.service.account.AccountService;

public class LgoExchange extends BaseExchange {

  private LgoSignatureService signatureService;
  private LgoProducts products;
  private LgoCurrencies currencies;

  @Override
  protected void initServices() {
    signatureService = LgoSignatureService.createInstance(getExchangeSpecification());
    this.marketDataService = new LgoMarketDataService(this);
    this.tradeService = new LgoTradeService(this, new LgoKeyService(getExchangeSpecification()));
    this.accountService = new AccountService() {};
  }

  @Override
  public void remoteInit() throws IOException {
    try {
      products = getMarketDataService().getLgoProducts();
      currencies = getMarketDataService().getLgoCurrencies();
    } catch (LgoException e) {
      throw LgoErrorAdapter.adapt(e);
    }
    LgoAdapters.adaptMetadata(getExchangeMetaData(), products, currencies);
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {
    return LgoEnv.prod();
  }

  @Override
  public LgoMarketDataService getMarketDataService() {
    return (LgoMarketDataService) marketDataService;
  }

  @Override
  public LgoTradeService getTradeService() {
    return (LgoTradeService) super.getTradeService();
  }

  public LgoProducts getProducts() {
    return products;
  }

  public LgoCurrencies getCurrencies() {
    return currencies;
  }

  public LgoSignatureService getSignatureService() {
    return signatureService;
  }
}
