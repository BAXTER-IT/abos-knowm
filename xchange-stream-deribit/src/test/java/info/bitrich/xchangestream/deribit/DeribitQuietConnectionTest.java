package info.bitrich.xchangestream.deribit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.timeout.IdleStateEvent;
import org.junit.jupiter.api.Test;

/**
 * ABOS hub #906: Deribit sends a heartbeat every 30 s once we ask for it. Its idle check fires after
 * 45 s of silence, so a healthy connection never reaches it, and once a heartbeat has arrived the
 * connection is closed at 90 s: at least two missed heartbeats. Without any heartbeat (Deribit
 * refused it) the connection is never closed by this rule, which would only loop.
 */
class DeribitQuietConnectionTest {

  private static final String TEST_REQUEST =
      "{\"jsonrpc\":\"2.0\",\"method\":\"heartbeat\",\"params\":{\"type\":\"test_request\"}}";

  private static class Service extends DeribitStreamingService {
    Service() {
      super("ws://127.0.0.1:9100/test");
      setAutoReconnect(false); // the reconnect itself is the existing path, not under test here
    }

    @Override
    public void sendMessage(String message) {}

    int idleSeconds() {
      return getIdleTimeoutSeconds();
    }

    EmbeddedChannel channel() {
      WebSocketClientHandshaker handshaker = mock(WebSocketClientHandshaker.class);
      when(handshaker.isHandshakeComplete()).thenReturn(true);
      return new EmbeddedChannel(getWebSocketClientHandler(handshaker, message -> {}));
    }
  }

  private static void silence(EmbeddedChannel channel, int checks) {
    channel.pipeline().fireUserEventTriggered(IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT);
    for (int i = 1; i < checks; i++) {
      channel.pipeline().fireUserEventTriggered(IdleStateEvent.READER_IDLE_STATE_EVENT);
    }
  }

  @Test
  void theIdleCheckFiresAfter45Seconds_aboveThe30SecondHeartbeat() {
    assertEquals(45, new Service().idleSeconds());
  }

  @Test
  void onceAHeartbeatHasArrived_silentFor45SecondsStaysOpen_silentFor90SecondsIsClosed() {
    Service service = new Service();
    EmbeddedChannel channel = service.channel();
    service.enableHeartbeat();
    service.messageHandler(TEST_REQUEST);

    silence(channel, 1); // 45 s
    assertTrue(channel.isOpen());

    channel.pipeline().fireUserEventTriggered(IdleStateEvent.READER_IDLE_STATE_EVENT); // 90 s
    assertFalse(channel.isOpen());
  }

  @Test
  void withoutAnyHeartbeat_theConnectionIsNeverClosedByThisRule() {
    Service service = new Service();
    EmbeddedChannel channel = service.channel();
    service.enableHeartbeat(); // Deribit refuses or never answers it

    silence(channel, 20); // 15 minutes

    assertTrue(channel.isOpen());
  }
}
