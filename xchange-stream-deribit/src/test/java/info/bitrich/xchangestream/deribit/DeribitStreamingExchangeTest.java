package info.bitrich.xchangestream.deribit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import info.bitrich.xchangestream.deribit.config.Config;
import io.reactivex.rxjava3.core.Completable;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.junit.jupiter.api.BeforeEach;

class DeribitStreamingExchangeTest {

  private DeribitStreamingExchange exchange;
  private ExchangeSpecification specification;

  @BeforeEach
  void setUp() {
    exchange = new DeribitStreamingExchange();
    specification = exchange.getDefaultExchangeSpecification();
    specification.setShouldLoadRemoteMetaData(false);
  }

  @Test
  void usesTheProductionAddressWhenTheSpecificationSetsNone() {
    exchange.applySpecification(specification);

    assertEquals(Config.V2_WS_URL, exchange.getWsUrl());
  }

  @Test
  void usesTheWebsocketOverrideFromTheSpecification() {
    specification.setOverrideWebsocketApiUri("ws://127.0.0.1:9100/test-exchange/deribit");
    exchange.applySpecification(specification);

    assertEquals("ws://127.0.0.1:9100/test-exchange/deribit", exchange.getWsUrl());
  }

  @Test
  void usesTheWsEndpointFromTheSpecificationWhenNoOverrideIsSet() {
    specification.setWsEndpoint("ws://127.0.0.1:9100/test-exchange/deribit");
    exchange.applySpecification(specification);

    assertEquals("ws://127.0.0.1:9100/test-exchange/deribit", exchange.getWsUrl());
  }

  @Test
  void prefersTheOverrideOverTheWsEndpoint() {
    specification.setOverrideWebsocketApiUri("ws://127.0.0.1:9100/override");
    specification.setWsEndpoint("ws://127.0.0.1:9100/ws-endpoint");
    exchange.applySpecification(specification);

    assertEquals("ws://127.0.0.1:9100/override", exchange.getWsUrl());
  }

  /** Records lifecycle calls instead of opening a real socket. */
  private static class RecordingPrivateService extends DeribitPrivateStreamingService {
    boolean connected;
    boolean disconnected;

    RecordingPrivateService() {
      super("ws://127.0.0.1:9100/test", "key", "secret");
    }

    @Override
    public Completable connect() {
      return Completable.fromAction(() -> connected = true);
    }

    @Override
    public Completable disconnect() {
      return Completable.fromAction(() -> disconnected = true);
    }
  }

  private DeribitStreamingExchange exchangeWith(RecordingPrivateService fake) {
    DeribitStreamingExchange fakeBacked = new DeribitStreamingExchange() {
      @Override
      DeribitPrivateStreamingService createPrivateStreamingService(String wsUrl) {
        return fake;
      }
    };
    ExchangeSpecification spec = fakeBacked.getDefaultExchangeSpecification();
    spec.setShouldLoadRemoteMetaData(false);
    fakeBacked.applySpecification(spec);
    return fakeBacked;
  }

  @Test
  void connectOpensOnlyThePrivateSocket() {
    RecordingPrivateService fake = new RecordingPrivateService();
    DeribitStreamingExchange fakeBacked = exchangeWith(fake);

    fakeBacked.connect().blockingAwait();

    assertTrue(fake.connected);
    assertSame(fake, fakeBacked.getPrivateStreamingService());
    assertNotNull(fakeBacked.getStreamingTradeService());
  }

  @Test
  void disconnectClosesThePrivateSocket() {
    RecordingPrivateService fake = new RecordingPrivateService();
    DeribitStreamingExchange fakeBacked = exchangeWith(fake);
    fakeBacked.connect().blockingAwait();

    fakeBacked.disconnect().blockingAwait();

    assertTrue(fake.disconnected);
    assertNull(fakeBacked.getPrivateStreamingService());
    assertNull(fakeBacked.getStreamingTradeService());
  }

  @Test
  void marketDataIsNotServedByThisConnector() {
    assertThrows(NotYetImplementedForExchangeException.class, exchange::getStreamingMarketDataService);
  }
}
