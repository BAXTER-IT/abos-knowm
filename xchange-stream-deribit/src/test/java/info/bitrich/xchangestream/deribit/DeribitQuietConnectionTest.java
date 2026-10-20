package info.bitrich.xchangestream.deribit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.timeout.IdleStateEvent;
import org.junit.jupiter.api.Test;

/**
 * ABOS hub #906: Deribit sends a heartbeat every 30 s. Its idle check fires after 45 s of silence,
 * so a healthy connection never reaches it, and the connection is closed at 90 s: at least two
 * heartbeats missed.
 */
class DeribitQuietConnectionTest {

  private static class Service extends DeribitStreamingService {
    Service() {
      super("ws://127.0.0.1:9100/test");
      setAutoReconnect(false); // the reconnect itself is the existing path, not under test here
    }

    EmbeddedChannel channel() {
      return new EmbeddedChannel(
          getWebSocketClientHandler(mock(WebSocketClientHandshaker.class), message -> {}));
    }
  }

  @Test
  void theIdleCheckSitsAboveTheHeartbeat_soAHealthyConnectionNeverTriggersIt() {
    int heartbeat = DeribitStreamingService.HEARTBEAT_INTERVAL_SECONDS;
    assertTrue(heartbeat * 3 / 2 > heartbeat);
  }

  @Test
  void silentFor45Seconds_staysOpen_silentFor90Seconds_isClosed() {
    EmbeddedChannel channel = new Service().channel();

    channel.pipeline().fireUserEventTriggered(IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT); // 45 s
    assertTrue(channel.isOpen());

    channel.pipeline().fireUserEventTriggered(IdleStateEvent.READER_IDLE_STATE_EVENT); // 90 s
    assertFalse(channel.isOpen());
  }
}
