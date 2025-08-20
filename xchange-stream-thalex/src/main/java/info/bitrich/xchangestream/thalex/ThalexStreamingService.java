package info.bitrich.xchangestream.thalex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.netty.WebSocketClientCompressionAllowClientNoContextAndServerNoContextHandler;
import info.bitrich.xchangestream.thalex.dto.ThalexWsLoginMessage;
import info.bitrich.xchangestream.thalex.dto.ThalexWsPingMessage;
import info.bitrich.xchangestream.thalex.dto.ThalexWsSubscribeMessage;
import info.bitrich.xchangestream.thalex.dto.ThalexWsUnsubscribeMessage;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.thalex.config.JwtGenerator;
import org.knowm.xchange.thalex.utils.TokenUtil;

@Slf4j
public class ThalexStreamingService extends JsonNettyStreamingService {

  private final ExchangeSpecification exchangeSpecification;

  private final Observable<Long> pingPongSrc = Observable.interval(30, 30, TimeUnit.SECONDS);
  private final JwtGenerator tokenUtil = new TokenUtil();
  private Disposable pingPongSubscription;

  public ThalexStreamingService(String apiUrl, ExchangeSpecification exchangeSpecification) {
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
  public void resubscribeChannels() {
    String jwt;
    try {
      jwt = tokenUtil.generate(exchangeSpecification.getApiKey(),
          exchangeSpecification.getSecretKey());
    } catch (Exception e) {
      log.error("Cannot generate JWT token to login to Websocket.", e);
      return;
    }
    ThalexWsLoginMessage message = new ThalexWsLoginMessage(jwt);
    try {
      sendMessage(objectMapper.writeValueAsString(message));
      super.resubscribeChannels();
    } catch (JsonProcessingException e) {
      log.error("Failed to log in to Thalex websocket. Won't resubscribe!", e);
    }
  }

  @Override
  protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
    return WebSocketClientCompressionAllowClientNoContextAndServerNoContextHandler.INSTANCE;
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) {
    JsonNode channelNameNode = message.get("channel_name");
    if (channelNameNode == null) {
      log.debug("Channel name is not present, misc message: {}", message);
      return null;
    }
    return channelNameNode.asText();
  }

  private String getPingMessage() throws JsonProcessingException {
    return objectMapper.writeValueAsString(new ThalexWsPingMessage());
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    return objectMapper.writeValueAsString(
        new ThalexWsSubscribeMessage(channelName));
  }

  @Override
  public String getUnsubscribeMessage(String channelName, Object... args) throws IOException {
    return objectMapper.writeValueAsString(
        new ThalexWsUnsubscribeMessage(channelName));
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
