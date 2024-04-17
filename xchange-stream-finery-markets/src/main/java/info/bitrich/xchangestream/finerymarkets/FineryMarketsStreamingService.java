package info.bitrich.xchangestream.finerymarkets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.netty.WebSocketClientCompressionAllowClientNoContextAndServerNoContextHandler;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.enums.HashingAlgorithm;
import org.knowm.xchange.exceptions.SignatureFieldsUnsetException;
import org.knowm.xchange.finerymarkets.streaming.dtos.FineryMarketsStreamingOrderRequestDto;
import org.knowm.xchange.finerymarkets.streaming.dtos.FineryMarketsStreamingPingDto;
import org.knowm.xchange.finerymarkets.streaming.enums.Event;
import org.knowm.xchange.utils.SignatureCreator;

@Slf4j
public class FineryMarketsStreamingService extends JsonNettyStreamingService {

  public static final String HEADER_EFX_KEY = "EFX-Key";
  public static final String HEADER_EFX_SIGNATURE = "EFX-Sign";
  public static final String HEADER_EFX_CONTENT = "EFX-Content";

  private final ExchangeSpecification exchangeSpecification;

  private final Observable<Long> pingPongSrc = Observable.interval(30, 30, TimeUnit.SECONDS);
  private Disposable pingPongSubscription;

  public FineryMarketsStreamingService(String apiUrl, ExchangeSpecification exchangeSpecification) {
    super(apiUrl);
    this.exchangeSpecification = exchangeSpecification;
  }

  @Override
  public Completable connect() {
    Completable conn = super.connect();
    return conn.andThen(
        (CompletableSource)
            completable -> {
              try {
                if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
                  pingPongSubscription.dispose();
                }

                pingPongSubscription =
                    pingPongSrc.subscribe(o -> this.sendMessage(getPingMessage()));

                completable.onComplete();
              } catch (Exception e) {
                completable.onError(e);
              }
            });
  }

  @Override
  protected void handleMessage(JsonNode message) {
    boolean hasErrorCode = message.get(3).isNumber() && message.get(3).intValue() > 0;
    if (hasErrorCode) {
      super.handleError(message, new Exception("Failed to subscribe"));
      return;
    }
    super.handleMessage(message);
  }

  @Override
  protected DefaultHttpHeaders getCustomHeaders() {
    DefaultHttpHeaders customHeaders = new DefaultHttpHeaders();
    customHeaders.add(HEADER_EFX_KEY, exchangeSpecification.getApiKey());
    String content = generateContent();
    customHeaders.add(HEADER_EFX_CONTENT, content);
    customHeaders.add(HEADER_EFX_SIGNATURE, generateSignature(content));
    return customHeaders;
  }

  private String generateSignature(String toSign) {
    try {
      return new SignatureCreator()
          .withHashingAlgorithm(HashingAlgorithm.SHA384)
          .withInformation(toSign)
          .withSecret(exchangeSpecification.getSecretKey())
          .create();
    } catch (SignatureFieldsUnsetException | NoSuchAlgorithmException | InvalidKeyException e) {
      log.error("Failed to generate signature", e);
      return "";
    }
  }

  private String generateContent() {
    HashMap<String, Object> content = new HashMap<>();
    Long timestamp = System.currentTimeMillis();
    content.put("nonce", timestamp);
    content.put("timestamp", timestamp);
    try {
      return objectMapper.writeValueAsString(content);
    } catch (JsonProcessingException e) {
      return "{}";
    }
  }

  @Override
  protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
    return WebSocketClientCompressionAllowClientNoContextAndServerNoContextHandler.INSTANCE;
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) {
    String feedId = message.get(1).asText();
    if (!"0".equals(feedId)) {
      return feedId;
    }
    return FineryMarketsStreamingTradeService.CHANNEL;
  }

  private String getPingMessage() throws JsonProcessingException {
    return objectMapper.writeValueAsString(new FineryMarketsStreamingPingDto());
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    return objectMapper.writeValueAsString(
        new FineryMarketsStreamingOrderRequestDto(Event.SUBSCRIBE, getFeedId(args)));
  }

  private String getFeedId(Object... args) {
    String feedId = null;
    if (args.length > 0 && args[0] instanceof CurrencyPair) {
      feedId = args[0].toString().replace("/", "-");
    }
    return feedId;
  }

  @Override
  public String getUnsubscribeMessage(String channelName, Object... args) throws IOException {
    return objectMapper.writeValueAsString(
        new FineryMarketsStreamingOrderRequestDto(Event.UNSUBSCRIBE, getFeedId(args)));
  }

  @Override
  public boolean processArrayMessageSeparately() {
    return false;
  }

  public void pingPongDisconnectIfConnected() {
    if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
      pingPongSubscription.dispose();
    }
  }
}
