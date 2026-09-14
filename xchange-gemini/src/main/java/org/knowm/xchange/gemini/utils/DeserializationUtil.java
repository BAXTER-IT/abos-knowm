package org.knowm.xchange.gemini.utils;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DeserializationUtil {


  public static String getText(JsonNode node, String fieldName) {
    return node.hasNonNull(fieldName) ? node.get(fieldName).asText() : null;
  }

  public static BigDecimal getBigDecimal(JsonNode node, String fieldName) {
    return node.hasNonNull(fieldName) ? new BigDecimal(node.get(fieldName).asText()) : null;
  }

  public static Long getLong(JsonNode node, String fieldName) {
    return node.hasNonNull(fieldName) ? node.get(fieldName).asLong() : null;
  }

  public static Boolean getBoolean(JsonNode node, String fieldName) {
    return node.hasNonNull(fieldName) ? node.get(fieldName).asBoolean() : null;
  }

  public static Integer getInt(JsonNode node, String fieldName) {
    return node.hasNonNull(fieldName) ? node.get(fieldName).asInt() : null;
  }
}

