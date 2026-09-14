package org.knowm.xchange.kucoin.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.knowm.xchange.kucoin.dto.response.TradeResponse;

/**
 * Reads a fill row into the typed {@link TradeResponse} and keeps the whole row on {@code
 * rawJson}. The typed DTO drops fields it does not know (the futures host adds tradeType,
 * settleCurrency, tradeTime, ...); the golden record needs them, so it stores rawJson.
 *
 * <p>rawJson is the parsed row written back out (same as the Bitget and Thalex captures): every
 * field in its original order, whitespace dropped. KuCoin sends its amounts as strings, so their
 * text is untouched; only whole numbers and booleans are re-rendered.
 */
public class TradeResponseDeserializer extends JsonDeserializer<TradeResponse> {

  /**
   * The caller's own mapper (the REST client's, in production) with this deserializer switched
   * off, so the typed fields keep every setting the rest of the module gets. One copy per mapper,
   * taken on first use; weak keys, so a mapper that is gone does not pin its copy.
   */
  private static final Map<ObjectMapper, ObjectMapper> TYPED_FIELDS =
      Collections.synchronizedMap(new WeakHashMap<>());

  @JsonDeserialize(using = JsonDeserializer.None.class)
  private interface DefaultMapping {}

  @Override
  public TradeResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    ObjectMapper mapper = (ObjectMapper) p.getCodec();
    JsonNode node = mapper.readTree(p);
    TradeResponse trade = typedFields(mapper).treeToValue(node, TradeResponse.class);
    trade.setRawJson(node.toString());
    return trade;
  }

  private static ObjectMapper typedFields(ObjectMapper mapper) {
    return TYPED_FIELDS.computeIfAbsent(
        mapper, m -> m.copy().addMixIn(TradeResponse.class, DefaultMapping.class));
  }
}
