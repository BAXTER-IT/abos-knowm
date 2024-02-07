package info.bitrich.xchangestream.service.netty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;

/**
 * This class should be merged with ObjectMapperHelper from XStream..
 *
 * @author Nikita Belenkiy on 19/06/2018.
 */
public class StreamingObjectMapperHelper {

  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(
        DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
    objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    objectMapper
        .coercionConfigFor(LogicalType.Enum)
        .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
    objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
  }

  private StreamingObjectMapperHelper() {}

  public static ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
