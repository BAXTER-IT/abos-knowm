package info.bitrich.xchangestream.service.netty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.timeout.IdleStateEvent;
import org.junit.jupiter.api.Test;

/**
 * ABOS hub #906: a connection that receives nothing for longer than its limit is closed, so the
 * normal reconnect takes over. The idle check fires every 15 s of silence here, as by default.
 */
class QuietConnectionCloseTest {

  static class Service extends NettyStreamingService<String> {
    Service(int quietCloseSeconds) {
      super("wss://venue.example/ws", 65536, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_RETRY_DURATION, 15);
      setQuietCloseSeconds(quietCloseSeconds);
      setAutoReconnect(false); // the reconnect itself is the existing path, not under test here
    }

    EmbeddedChannel channel() {
      return channel(true);
    }

    EmbeddedChannel channel(boolean opened) {
      WebSocketClientHandshaker handshaker = mock(WebSocketClientHandshaker.class);
      when(handshaker.isHandshakeComplete()).thenReturn(opened);
      return new EmbeddedChannel(getWebSocketClientHandler(handshaker, message -> {}));
    }

    @Override
    protected String getChannelNameFromMessage(String message) {
      return null;
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) {
      return null;
    }

    @Override
    public String getUnsubscribeMessage(String channelName, Object... args) {
      return null;
    }

    @Override
    public void messageHandler(String message) {}
  }

  private static void silence(EmbeddedChannel channel, int checks) {
    channel.pipeline().fireUserEventTriggered(IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT);
    for (int i = 1; i < checks; i++) {
      channel.pipeline().fireUserEventTriggered(IdleStateEvent.READER_IDLE_STATE_EVENT);
    }
  }

  @Test
  void aConnectionSilentForItsLimit_isClosed_andNotBefore() {
    EmbeddedChannel channel = new Service(60).channel();

    silence(channel, 3); // 45 s
    assertThat(channel.isOpen()).isTrue();

    channel.pipeline().fireUserEventTriggered(IdleStateEvent.READER_IDLE_STATE_EVENT); // 60 s
    assertThat(channel.isOpen()).isFalse();
  }

  @Test
  void anyMessageStartsTheCountAgain_soAHealthyConnectionIsNeverClosed() {
    EmbeddedChannel channel = new Service(60).channel();

    for (int round = 0; round < 10; round++) {
      silence(channel, 3); // 45 s of silence, then something arrives and the next check is "first"
    }

    assertThat(channel.isOpen()).isTrue();
  }

  @Test
  void offByDefault_theCheckOnlyPings_asBefore() {
    EmbeddedChannel channel = new Service(0).channel();

    silence(channel, 20); // 5 minutes

    assertThat(channel.isOpen()).isTrue();
    assertThat((Object) channel.readOutbound()).isInstanceOf(PingWebSocketFrame.class);
  }

  @Test
  void aConnectionStillOpening_isLeftToTheCallersConnectLimit() {
    EmbeddedChannel channel = new Service(60).channel(false);

    silence(channel, 8); // 2 minutes, the websocket never finished opening

    assertThat(channel.isOpen()).isTrue();
  }
}
