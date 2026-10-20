package info.bitrich.xchangestream.kucoin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.junit.jupiter.api.Test;

/**
 * ABOS hub #907: a reconnect of either websocket is reported. With concat only the first service's
 * reconnects ever arrived, because its stream never ends.
 */
class KucoinReconnectSignalTest {

  @Test
  void aReconnectOfTheSecondService_isReportedToo() {
    KucoinStreamingExchange exchange = new KucoinStreamingExchange();
    PublishSubject<Object> publicReconnects = PublishSubject.create();
    PublishSubject<Object> privateReconnects = PublishSubject.create();
    exchange.connectService(service(publicReconnects), "public", 1_000);
    exchange.connectService(service(privateReconnects), "private", 1_000);

    TestObserver<Object> reported = exchange.connectionSuccess().test();
    privateReconnects.onNext("private reconnected");
    publicReconnects.onNext("public reconnected");

    assertThat(reported.values()).containsExactly("private reconnected", "public reconnected");
  }

  private static KucoinStreamingService service(PublishSubject<Object> reconnects) {
    KucoinStreamingService service = mock(KucoinStreamingService.class);
    when(service.connect()).thenReturn(Completable.complete());
    when(service.subscribeConnectionSuccess()).thenReturn(reconnects);
    return service;
  }
}
