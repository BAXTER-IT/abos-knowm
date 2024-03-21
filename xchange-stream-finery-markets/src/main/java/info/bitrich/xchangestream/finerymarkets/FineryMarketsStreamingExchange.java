package info.bitrich.xchangestream.finerymarkets;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import io.reactivex.Completable;
import lombok.Getter;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.finerymarkets.FineryMarketsExchange;
import org.knowm.xchange.fixprotocol.FixParameters;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;

public class FineryMarketsStreamingExchange extends FineryMarketsExchange
    implements StreamingExchange {

  private static final String CONFIG_FILE_NAME = "FineryMarketsFixConfiguration.cfg";
  @Getter FineryMarketsFixApplication application;
  private SocketInitiator initiator;
  @Getter private FineryMarketsStreamingTradeService streamingTradeService;

  public FineryMarketsStreamingExchange(FixParameters fixParameters) {
    application = new FineryMarketsFixApplication(this);
    streamingTradeService = new FineryMarketsStreamingTradeService(this);
  }

  @Override
  protected void initServices() {
    // No need to initialize REST services
  }

  @Override
  public Completable connect(ProductSubscription... args) {
    return Completable.create(
        emitter -> {
          try {
            connectToFix();
            emitter.onComplete();
          } catch (Exception e) {
            emitter.onError(e);
          }
        });
  }

  @Override
  public Completable disconnect() {
    return Completable.create(
        emitter -> {
          if (initiator != null) {
            initiator.stop();
          }
          emitter.onComplete();
        });
  }

  @Override
  public boolean isAlive() {
    return initiator != null && initiator.isLoggedOn();
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    // Not needed
  }

  private void connectToFix() throws ConfigError {
    SessionSettings settings = new SessionSettings(CONFIG_FILE_NAME);

    // Add parameters to the [default] session
    settings.setString("Username", exchangeSpecification.getApiKey());
    settings.setString("SenderCompID", exchangeSpecification.getApiKey());

    MessageStoreFactory storeFactory = new FileStoreFactory(settings);
    LogFactory logFactory = new ScreenLogFactory(settings);
    DefaultMessageFactory messageFactory = new DefaultMessageFactory();

    initiator =
        new SocketInitiator(application, storeFactory, settings, logFactory, messageFactory);
    initiator.start();
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {
    ExchangeSpecification exchangeSpecification = super.getDefaultExchangeSpecification();
    exchangeSpecification.setHost("HOST");
    exchangeSpecification.setPort(1111111111);
    return exchangeSpecification;
  }

  @Override
  public void applySpecification(ExchangeSpecification exchangeSpecification) {
    if (useSandbox(exchangeSpecification)) {
      exchangeSpecification.setHost("TEST_HOST");
      exchangeSpecification.setPort(222222222);
    }
    super.applySpecification(exchangeSpecification);
  }
}
