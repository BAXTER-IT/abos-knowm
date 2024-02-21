package info.bitrich.xchangestream.bybit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import lombok.Data;

@Data
@JsonPropertyOrder(value = {"op", "args"})
public class BybitStreamingDto {

  List<Object> args;
  private Op op;

  public BybitStreamingDto(Op op, List<Object> args) {
    this.op = op;
    this.args = args;
  }

  public enum Op {
    PING("ping"),
    AUTH("auth"),
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe");

    private final String value;

    Op(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }
  }
}
