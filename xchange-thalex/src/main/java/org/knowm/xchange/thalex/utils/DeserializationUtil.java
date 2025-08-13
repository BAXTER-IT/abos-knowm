package org.knowm.xchange.thalex.utils;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DeserializationUtil {


  public static String getText(JsonNode node, String fieldName) {
    return node.hasNonNull(fieldName) ? node.get(fieldName).asText() : null;
  }

  public static BigDecimal getBigDecimal(JsonNode node, String fieldName) {
    return node.hasNonNull(fieldName) ? new BigDecimal(node.get(fieldName).asText()) : null;
  }

  public static Instant getInstant(JsonNode node) {
    return node.hasNonNull("time") ? Instant.ofEpochSecond(node.get("time").asLong()) : null;
  }
}
