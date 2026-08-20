package info.bitrich.xchangestream.deribit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import info.bitrich.xchangestream.deribit.config.Config;
import info.bitrich.xchangestream.deribit.dto.request.DeribitWsRequest;
import info.bitrich.xchangestream.deribit.dto.request.DeribitWsRequest.Method;
import info.bitrich.xchangestream.deribit.dto.request.DeribitWsRequest.Params;
import info.bitrich.xchangestream.deribit.dto.response.DeribitEventNotification;
import info.bitrich.xchangestream.deribit.dto.response.DeribitWsNotification;
import info.bitrich.xchangestream.service.netty.NettyStreamingService;
import io.reactivex.rxjava3.core.Completable;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeribitStreamingService extends NettyStreamingService<DeribitWsNotification> {

  /** Deribit accepts 10-300s; 30s matches the rate distributor's proven setting. */
  static final int HEARTBEAT_INTERVAL_SECONDS = 30;

  protected final ObjectMapper objectMapper = Config.getInstance().getObjectMapper();

  public DeribitStreamingService(String apiUri) {
    super(apiUri, Integer.MAX_VALUE);
  }

  /**
   * Arms Deribit's application heartbeat after every completed open (first connect and every
   * auto-reconnect). Protocol-level ping frames do not count as activity for Deribit's heartbeat
   * mechanism, so without this the server drops the connection after the heartbeat timeout.
   */
  @Override
  protected Completable openConnection() {
    return super.openConnection().doOnComplete(this::enableHeartbeat);
  }

  void enableHeartbeat() {
    ObjectNode request = objectMapper.createObjectNode();
    request.put("jsonrpc", "2.0");
    request.put("method", "public/set_heartbeat");
    request.putObject("params").put("interval", HEARTBEAT_INTERVAL_SECONDS);
    sendMessage(request.toString());
  }

  @Override
  protected String getChannelNameFromMessage(DeribitWsNotification message) {
      return message.getParams().getChannel();
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    var deribitWsRequest = DeribitWsRequest.builder()
        .method(Method.SUBSCRIBE)
        .params(Params.builder().channels(List.of(channelName)).build())
        .build();
    return objectMapper.writeValueAsString(deribitWsRequest);
  }

  @Override
  public String getUnsubscribeMessage(String channelName, Object... args) throws IOException {
    var deribitWsRequest = DeribitWsRequest.builder()
        .method(Method.UNSUBSCRIBE)
        .params(Params.builder().channels(List.of(channelName)).build())
        .build();
    return objectMapper.writeValueAsString(deribitWsRequest);
  }

  @Override
  protected void handleMessage(DeribitWsNotification message) {
    log.debug("Processing {}", message.toString());
    // no special processing of event messages
    if (message instanceof DeribitEventNotification) {
      return;
    }
    // No channel means nothing downstream can route it, and the channel lookup cannot take
    // null. Drop the frame instead of letting an exception kill the socket.
    if (message.getParams() == null || message.getParams().getChannel() == null) {
      log.warn("Dropping a Deribit frame with no channel: {}", message);
      return;
    }
    super.handleMessage(message);
  }

  @Override
  public void messageHandler(String message) {
    log.debug("Received message: {}", message);
    DeribitWsNotification deribitWsNotification;

    // Parse incoming message to JSON
    try {
      JsonNode jsonNode = objectMapper.readTree(message);

      // Answered at the raw layer; a heartbeat never reaches a channel.
      if ("heartbeat".equals(jsonNode.path("method").asText())) {
        if ("test_request".equals(jsonNode.path("params").path("type").asText())) {
          ObjectNode reply = objectMapper.createObjectNode();
          reply.put("jsonrpc", "2.0");
          reply.put("method", "public/test");
          reply.putObject("params");
          sendMessage(reply.toString());
        }
        return;
      }

      // A refusal ({"error":...}) carries no params and must not reach the channel lookup:
      // an exception escaping from there closes the socket and loops reconnects.
      if (jsonNode.has("error")) {
        log.warn("Deribit refused a request: {}", jsonNode.get("error"));
        return;
      }

      // try to parse event
      if (jsonNode.has("result")) {
        ((ObjectNode) jsonNode).put("messageType", "event");
      }
      // copy nested value of params.channel to the root of json to detect deserialization type
      else if (jsonNode.has("params") && jsonNode.get("params").has("channel")) {
        var channelWords = jsonNode.get("params").get("channel").asText().split("\\.");
        var channelText = channelWords[0];

        // if name starts with 'user.' it is a 2-words name
        if ("user".equals(channelWords[0]) && channelWords.length > 1) {
          channelText += "." + channelWords[1];
        }

        ((ObjectNode) jsonNode).put("messageType", channelText);
      }

      deribitWsNotification = objectMapper.treeToValue(jsonNode, DeribitWsNotification.class);

    } catch (IOException e) {
      log.error("Error parsing incoming message to JSON: {}", message);
      log.error(e.getMessage(), e);
      return;
    }

    if (deribitWsNotification.hasSinglePayload()) {
      handleMessage(deribitWsNotification);
    } else {
      // process several payloads separately
      ((List) deribitWsNotification.getParams().getData()).stream().forEach(payload -> {
        var singleNotification = deribitWsNotification.toBuilder().build();
        singleNotification.getParams().setData(List.of(payload));
        handleMessage(singleNotification);
      });
    }
  }
}
