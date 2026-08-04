package org.knowm.xchange.deribit.v2.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.deribit.v2.config.DeribitJacksonObjectMapperFactory;

public class KindTest {

  /** The mapper the connector actually binds responses with. */
  private static ObjectMapper mapper() {
    ObjectMapper mapper = new ObjectMapper();
    new DeribitJacksonObjectMapperFactory().configureObjectMapper(mapper);
    return mapper;
  }

  /**
   * {@code Kind} is passed as a {@code @QueryParam}, which rescu renders through {@code
   * String.valueOf(Object)} — Jackson's {@code @JsonProperty} is not consulted on that path. The
   * constant names are upper-case plurals and Deribit expects lower-case singulars, so without a
   * {@code toString()} override the request goes out as {@code kind=OPTIONS} and is rejected with
   * {@code -32602: Invalid params, {reason=invalid value, param=kind}}.
   */
  @Test
  public void rendersDeribitWireValueAsQueryParam() {
    assertThat(String.valueOf(Kind.FUTURES)).isEqualTo("future");
    assertThat(String.valueOf(Kind.OPTIONS)).isEqualTo("option");
    assertThat(String.valueOf(Kind.SPOT)).isEqualTo("spot");
    assertThat(String.valueOf(Kind.FUTURES_COMBO)).isEqualTo("future_combo");
    assertThat(String.valueOf(Kind.OPTIONS_COMBO)).isEqualTo("option_combo");
  }

  /** The toString() override must not disturb the Jackson binding used for response fields. */
  @Test
  public void stillDeserializesFromDeribitWireValue() throws Exception {
    ObjectMapper mapper = mapper();

    assertThat(mapper.readValue("\"future\"", Kind.class)).isEqualTo(Kind.FUTURES);
    assertThat(mapper.readValue("\"option\"", Kind.class)).isEqualTo(Kind.OPTIONS);
    assertThat(mapper.readValue("\"spot\"", Kind.class)).isEqualTo(Kind.SPOT);
    assertThat(mapper.readValue("\"future_combo\"", Kind.class)).isEqualTo(Kind.FUTURES_COMBO);
    assertThat(mapper.readValue("\"option_combo\"", Kind.class)).isEqualTo(Kind.OPTIONS_COMBO);
  }

  /**
   * An unrecognised kind falls back rather than blowing up the response mapping. This relies on
   * {@code READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE}, which {@link
   * DeribitJacksonObjectMapperFactory} enables — a bare ObjectMapper throws instead.
   */
  @Test
  public void deserializesUnrecognisedKindToUnknown() throws Exception {
    ObjectMapper mapper = mapper();

    assertThat(mapper.readValue("\"something_new\"", Kind.class)).isEqualTo(Kind.UNKNOWN);
  }
}
