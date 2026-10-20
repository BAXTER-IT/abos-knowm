package info.bitrich.xchangestream.kucoin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Completable;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * ABOS hub #908: a KuCoin websocket connect gives up at its limit, and a failed one is switched off and
 * closed instead of lingering, redialling on its own threads.
 */
class KucoinStreamingExchangeConnectLimitTest {

  private final KucoinStreamingExchange exchange = new KucoinStreamingExchange();

  @Test
  void aConnectThatNeverFinishes_failsAtTheLimit_andIsSwitchedOffAndClosed() {
    KucoinStreamingService service = mock(KucoinStreamingService.class);
    when(service.connect()).thenReturn(Completable.never());
    when(service.disconnect()).thenReturn(Completable.complete());

    long start = System.nanoTime();
    assertThatThrownBy(() -> exchange.connectService(service, "private", 50))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("did not connect within");

    assertThat((System.nanoTime() - start) / 1_000_000).isLessThan(5_000);
    verify(service).setAutoReconnect(false);
    verify(service).disconnect();
    assertThat(exchange.isAlive()).isFalse();
  }

  @Test
  void aConnectThatFails_isSwitchedOffClosedAndRethrown() {
    KucoinStreamingService service = mock(KucoinStreamingService.class);
    when(service.connect()).thenReturn(Completable.error(new IllegalStateException("refused")));
    when(service.disconnect()).thenReturn(Completable.complete());

    assertThatThrownBy(() -> exchange.connectService(service, "private", 50)).hasMessage("refused");

    verify(service).setAutoReconnect(false);
    verify(service).disconnect();
  }

  @Test
  void aConnectThatSucceeds_staysRecorded_soDisconnectReachesIt() {
    KucoinStreamingService service = mock(KucoinStreamingService.class);
    when(service.connect()).thenReturn(Completable.complete());
    when(service.isSocketOpen()).thenReturn(true);
    when(service.disconnect()).thenReturn(Completable.complete());

    exchange.connectService(service, "private", 50);

    assertThat(exchange.isAlive()).isTrue();
    verify(service, never()).setAutoReconnect(false);
    exchange.disconnect().blockingAwait();
    verify(service).disconnect();
  }

  @Test
  void aRealServiceThatNeverConnected_closesWithoutCrashing() {
    KucoinStreamingService service = new KucoinStreamingService("wss://127.0.0.1:1/endpoint", 18_000, false);

    assertThat(service.disconnect().blockingAwait(5, TimeUnit.SECONDS)).isTrue();
  }

  @Test
  void aRealConnectThatIsRefused_failsWithTheConnectError_andLeavesNothingBehind() {
    KucoinStreamingService service = new KucoinStreamingService("wss://127.0.0.1:1/endpoint", 18_000, false);

    assertThatThrownBy(() -> exchange.connectService(service, "public", 5_000))
        .hasRootCauseInstanceOf(java.net.ConnectException.class);

    assertThat(exchange.isAlive()).isFalse();
    assertThat(service.isSocketOpen()).isFalse();
  }
}
