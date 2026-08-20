package info.bitrich.xchangestream.deribit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import info.bitrich.xchangestream.deribit.dto.response.DeribitWsNotification;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Pins the raw-layer handling of Deribit's heartbeat and refusal frames. */
class DeribitStreamingServiceHeartbeatTest {

  /** Captures outgoing messages and parsed notifications instead of using a socket. */
  private static class RecordingService extends DeribitStreamingService {
    final List<String> sent = new ArrayList<>();
    final List<DeribitWsNotification> handled = new ArrayList<>();

    RecordingService() {
      super("ws://127.0.0.1:9100/test");
    }

    @Override
    public void sendMessage(String message) {
      sent.add(message);
    }

    @Override
    protected void handleMessage(DeribitWsNotification message) {
      handled.add(message);
    }
  }

  @Test
  void answersATestRequestWithPublicTest() {
    RecordingService service = new RecordingService();

    service.messageHandler("{\"jsonrpc\":\"2.0\",\"method\":\"heartbeat\",\"params\":{\"type\":\"test_request\"}}");

    assertEquals(1, service.sent.size());
    assertTrue(service.sent.get(0).contains("\"method\":\"public/test\""));
    assertTrue(service.handled.isEmpty());
  }

  @Test
  void swallowsAnOrdinaryHeartbeatWithoutReplying() {
    RecordingService service = new RecordingService();

    service.messageHandler("{\"jsonrpc\":\"2.0\",\"method\":\"heartbeat\",\"params\":{\"type\":\"heartbeat\"}}");

    assertTrue(service.sent.isEmpty());
    assertTrue(service.handled.isEmpty());
  }

  @Test
  void armsTheHeartbeatWithTheAgreedInterval() {
    RecordingService service = new RecordingService();

    service.enableHeartbeat();

    assertEquals(1, service.sent.size());
    assertTrue(service.sent.get(0).contains("\"method\":\"public/set_heartbeat\""));
    assertTrue(service.sent.get(0).contains("\"interval\":" + DeribitStreamingService.HEARTBEAT_INTERVAL_SECONDS));
  }

  @Test
  void ordinaryMessagesStillReachTheChannelHandler() {
    RecordingService service = new RecordingService();

    service.messageHandler("{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":[]}");

    assertTrue(service.sent.isEmpty());
    assertEquals(1, service.handled.size());
  }

  /** Sends nowhere but keeps the real handleMessage, so routing guards are exercised. */
  private static class SendlessService extends DeribitStreamingService {
    SendlessService() {
      super("ws://127.0.0.1:9100/test");
    }

    @Override
    public void sendMessage(String message) {
      // dropped: no socket in these tests
    }
  }

  @Test
  void aRefusalIsLoggedAndDropped_neverAnExceptionThatKillsTheSocket() {
    SendlessService service = new SendlessService();

    service.messageHandler(
        "{\"jsonrpc\":\"2.0\",\"id\":8848,\"error\":{\"code\":13009,\"message\":\"unauthorized\"},\"testnet\":false}");
    // No assertion beyond "no exception": an escaped exception here closes the socket and
    // starts the reconnect loop this connector exists to avoid.
  }

  @Test
  void aChannelLessNotificationIsDropped_neverAnExceptionThatKillsTheSocket() {
    SendlessService service = new SendlessService();

    service.messageHandler("{\"jsonrpc\":\"2.0\",\"method\":\"subscription\",\"params\":{\"data\":[1]}}");
  }
}
