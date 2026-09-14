package org.knowm.xchange.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.Collection;
import lombok.NoArgsConstructor;

/**
 * A contextual Jackson deserializer that captures the exact JSON used to populate a field and
 * injects it into any object that implements {@link RawJsonAware}.
 * <p>
 * This deserializer is typically applied to a generic field. It supports both single objects and
 * collections:
 * <ul>
 *   <li>If the target value is a single {@link RawJsonAware}, its {@code rawJson} is set
 *       to the full JSON object representing {@code data}.</li>
 *   <li>If the target value is a {@link Collection}, each element that
 *       implements {@link RawJsonAware} receives the raw JSON of its own array element.</li>
 * </ul>
 * <p>
 * Internally, the deserializer:
 * <ol>
 *   <li>Reads the JSON subtree for the property into a {@link JsonNode}.</li>
 *   <li>Delegates normal deserialization to Jackson using the contextual type {@code T}.</li>
 *   <li>Post-processes the resulting object(s) to inject raw JSON where applicable.</li>
 * </ol>
 * <p>
 * This allows generic response wrappers to deserialize into structured POJOs
 * while still retaining the exact original JSON
 *
 * @param <T> the contextual target type Jackson resolves for the field being deserialized
 */
@NoArgsConstructor
public class RawJsonDataDeserializer<T>
    extends JsonDeserializer<T>
    implements ContextualDeserializer {

  private JsonDeserializer<Object> valueDeserializer = null;

  private RawJsonDataDeserializer(
      JsonDeserializer<Object> valueDeserializer) {
    this.valueDeserializer = valueDeserializer;
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
      BeanProperty property)
      throws JsonMappingException {

    JavaType type = (property != null)
        ? property.getType()
        : ctxt.getContextualType();

    JsonDeserializer<Object> deserializer =
        ctxt.findContextualValueDeserializer(type, property);

    return new RawJsonDataDeserializer<>(deserializer);
  }

  @Override
  @SuppressWarnings("unchecked")
  public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    ObjectCodec codec = p.getCodec();

    JsonNode node = codec.readTree(p);

    // Let Jackson deserialize T from that subtree
    JsonParser nodeParser = node.traverse(codec);
    nodeParser.nextToken(); // move to first token
    Object value = valueDeserializer.deserialize(nodeParser, ctxt);

    // single RawJsonAware object
    if (value instanceof RawJsonAware) {
      ((RawJsonAware) value).setRawJson(node.toString());
    }
    // list/collection of elements, some of which may be RawJsonAware
    else if (value instanceof Collection<?> && node.isArray()) {
      ArrayNode arrayNode = (ArrayNode) node;
      int i = 0;
      for (Object elem : (Collection<?>) value) {
        if (elem instanceof RawJsonAware && i < arrayNode.size()) {
          JsonNode elemNode = arrayNode.get(i);
          ((RawJsonAware) elem).setRawJson(elemNode.toString());
        }
        i++;
      }
    }

    return (T) value;
  }
}
