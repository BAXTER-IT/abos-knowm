package org.knowm.xchange.coincall.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.knowm.xchange.utils.jackson.RawJsonDataDeserializer;
import si.mazi.rescu.ExceptionalReturnContentException;

@Data
public class CoincallResponse<T> {

  @JsonProperty("code")
  private int code;

  @JsonProperty("msg")
  private String msg;

  @JsonProperty("i18nArgs")
  private Object i18nArgs;

  @JsonProperty("data")
  @JsonDeserialize(using = RawJsonDataDeserializer.class)
  private T data;

  @JsonCreator
  public CoincallResponse(@JsonProperty("code") int code,
      @JsonProperty("msg") String msg,
      @JsonProperty("i18nArgs") Object i18nArgs,
      @JsonProperty("data") T data) {
    if (code != 0) {
      // This is caught by rescu, handling response as error
      throw new ExceptionalReturnContentException("Error");
    }
    this.code = code;
    this.msg = msg;
    this.i18nArgs = i18nArgs;
    this.data = data;
  }
}
