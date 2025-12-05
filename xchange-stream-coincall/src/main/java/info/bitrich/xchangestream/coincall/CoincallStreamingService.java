package info.bitrich.xchangestream.coincall;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.coincall.dto.CoincallWsRequestDto;
import info.bitrich.xchangestream.coincall.dto.enums.CoincallChannelAction;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.netty.WebSocketClientCompressionAllowClientNoContextAndServerNoContextHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.enums.HashingAlgorithm;
import org.knowm.xchange.exceptions.SignatureFieldsUnsetException;
import org.knowm.xchange.utils.SignatureCreator;

@Slf4j
public class CoincallStreamingService extends JsonNettyStreamingService {

  private final ExchangeSpecification exchangeSpecification;

  private final Observable<Long> pingPongSrc = Observable.interval(28, 28, TimeUnit.SECONDS);
  private Disposable pingPongSubscription;

  public CoincallStreamingService(String apiUrl, ExchangeSpecification exchangeSpecification) {
    super(apiUrl);
    this.exchangeSpecification = exchangeSpecification;
  }

  public static URI rebuildUriWithParams(URI original, Map<String, String> params) {
    try {
      StringBuilder query = new StringBuilder();
      for (Map.Entry<String, String> e : params.entrySet()) {
        if (query.length() > 0) {
          query.append("&");
        }
        query.append(
                e.getKey()) // URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8.toString()))
            .append("=")
            .append(
                e.getValue()); // URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8.toString()));
      }

      return new URI(
          original.getScheme(),
          original.getAuthority(),
          original.getPath(),
          query.toString(),
          null
      );
    } catch (URISyntaxException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  @Override
  public Completable connect() {
    String ts = String.valueOf(System.currentTimeMillis());
    String toSign =
        "GET/users/self/verify?apiKey=" + exchangeSpecification.getApiKey() + "&ts=" + ts;
    String signature = generateSignature(toSign);

    // populating ws endpoint with auth params
    Map<String, String> params = new LinkedHashMap<>();

    if (!isSpotService()) {
      params.put("code", "10");
    }

    params.put("ts", ts);
    params.put("sign", signature);
    params.put("apiKey", exchangeSpecification.getApiKey());

    uri = rebuildUriWithParams(uri, params);

    Completable conn = super.connect();
    return conn.andThen(
            (CompletableSource)
                completable -> {
                  try {
                    if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
                      pingPongSubscription.dispose();
                    }

                    pingPongSubscription =
                        pingPongSrc.subscribe(o -> this.sendMessage(getHeartbeatMessage()));

                    completable.onComplete();
                  } catch (Exception e) {
                    completable.onError(e);
                  }
                })
        .andThen(
            (CompletableSource)
                completable -> {
                  resubscribeChannels();
                  completable.onComplete();
                });
  }

  boolean isSpotService() {
    return Stream.of("futures", "options").noneMatch(uri.getPath()::contains);
  }

  private String generateSignature(String toSign) {
    try {
      return new SignatureCreator()
          .withHashingAlgorithm(HashingAlgorithm.SHA256)
          .withInformation(toSign)
          .withSecret(exchangeSpecification.getSecretKey())
          .encodeAsHex()
          .uppercase()
          .create();
    } catch (SignatureFieldsUnsetException | NoSuchAlgorithmException | InvalidKeyException e) {
      return "";
    }
  }

  @Override
  protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
    return WebSocketClientCompressionAllowClientNoContextAndServerNoContextHandler.INSTANCE;
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) {
    log.info(message.toString());
    if (isSpotService()) {
      return message.path("ch").asText("");
    } else {
      int channelDataType = message.path("dt").asInt();
      switch (channelDataType) {
        case 12:
          return "position_options";
        case 27:
          return "positionEvent_options";
        case 36:
          return "position_futures";
        case 46:
          return "positionEvent_futures";
      }
    }
    return "";
  }

  private String getHeartbeatMessage() throws JsonProcessingException {
    return objectMapper.writeValueAsString(
        CoincallWsRequestDto.builder()
            .action(CoincallChannelAction.HEARTBEAT)
            .build());
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    CoincallWsRequestDto<?> dto;
    if (isSpotService()) {
      dto = CoincallWsRequestDto.builder()
          .sub(channelName)
          .id(String.valueOf(System.currentTimeMillis()))
          .build();
    } else {
      dto = CoincallWsRequestDto.builder()
          .action(CoincallChannelAction.SUBSCRIBE)
          .dataType(channelName)
          .build();
    }
    String s = objectMapper.writeValueAsString(dto);
    log.info(s);
    return s;
  }

  @Override
  public String getUnsubscribeMessage(String channelName, Object... args) throws IOException {
    return objectMapper.writeValueAsString(
        CoincallWsRequestDto.builder()
            .action(CoincallChannelAction.UNSUBSCRIBE)
            .dataType(channelName)
            .build());
  }

  public void pingPongDisconnectIfConnected() {
    if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
      pingPongSubscription.dispose();
    }
  }

  @Override
  public String getSubscriptionUniqueId(String channelName, Object... args) {
    String id = channelName;
    String product = null;
    if (args.length > 0 && args[0] != null) {
      product = args[0].toString();
    }
    if (product != null) {
      id += "_" + product;
    }
    return id;
  }
}
