package info.bitrich.xchangestream.gemini;

import static info.bitrich.xchangestream.gemini.Constants.CHANNEL_TRADE_HISTORY;
import static info.bitrich.xchangestream.gemini.Constants.HEADER_APIKEY;
import static info.bitrich.xchangestream.gemini.Constants.HEADER_PAYLOAD;
import static info.bitrich.xchangestream.gemini.Constants.HEADER_SIGNATURE;
import static info.bitrich.xchangestream.gemini.Constants.ORDER_EVENT_TYPE_HEARTBEAT;
import static info.bitrich.xchangestream.gemini.Constants.ORDER_EVENT_TYPE_SUBSCRIPTION_ACK;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import java.math.BigInteger;
import java.util.Base64;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.enums.HashingAlgorithm;
import org.knowm.xchange.utils.SignatureCreator;

@Slf4j
public class GeminiStreamingService extends JsonNettyStreamingService {

  private final ExchangeSpecification exchangeSpecification;

  public GeminiStreamingService(String apiUrl, ExchangeSpecification exchangeSpecification) {
    super(apiUrl);
    this.exchangeSpecification = exchangeSpecification;
  }

  @Override
  protected DefaultHttpHeaders getCustomHeaders() {
    DefaultHttpHeaders customHeaders = new DefaultHttpHeaders();
    customHeaders.add(HEADER_APIKEY, exchangeSpecification.getApiKey());
    String content = generateContent();
    customHeaders.add(HEADER_PAYLOAD, content);
    customHeaders.add(HEADER_SIGNATURE, generateSignature(content));
    return customHeaders;
  }

  String generateContent() {
    HashMap<String, Object> content = new HashMap<>();
    content.put("nonce", System.currentTimeMillis());
    content.put("request", CHANNEL_TRADE_HISTORY);
    try {
      return Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(content));
    } catch (JsonProcessingException e) {
      log.error("Failed to generate payload content", e);
      return "{}";
    }
  }

  String generateSignature(String toSign) {
    try {
      byte[] bytes = new SignatureCreator()
          .withHashingAlgorithm(HashingAlgorithm.SHA384)
          .withInformation(toSign)
          .withSecret(exchangeSpecification.getSecretKey())
          .createBytes();
      return String.format("%096x", new BigInteger(1, bytes));
    } catch (Exception e) {
      log.error("Failed to generate signature", e);
      return "";
    }
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) {
    return CHANNEL_TRADE_HISTORY;
  }

  @Override
  protected void handleMessage(JsonNode message) {
    String messageType = message.get("type").asText();
    if (ORDER_EVENT_TYPE_SUBSCRIPTION_ACK.equalsIgnoreCase(messageType)
        || ORDER_EVENT_TYPE_HEARTBEAT.equalsIgnoreCase(messageType)) {
      return;
    }
    super.handleMessage(message);
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) {
    return null;
  }

  @Override
  public String getUnsubscribeMessage(String channelName, Object... args) {
    return null;
  }

}
