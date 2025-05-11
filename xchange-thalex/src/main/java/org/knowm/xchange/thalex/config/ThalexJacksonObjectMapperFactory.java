package org.knowm.xchange.thalex.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import si.mazi.rescu.serialization.jackson.DefaultJacksonObjectMapperFactory;

public class ThalexJacksonObjectMapperFactory extends DefaultJacksonObjectMapperFactory {

  @Override
  public void configureObjectMapper(ObjectMapper objectMapper) {
    super.configureObjectMapper(objectMapper);

    // don't write nulls
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);

    // enable parsing to Instant
    objectMapper.registerModule(new JavaTimeModule());
  }
}
