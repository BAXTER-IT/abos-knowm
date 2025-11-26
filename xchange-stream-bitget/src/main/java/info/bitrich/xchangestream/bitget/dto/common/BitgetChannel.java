package info.bitrich.xchangestream.bitget.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@SuperBuilder
public class BitgetChannel {

  @JsonProperty("instType")
  private InstType instType;

  @JsonProperty("channel")
  private ChannelType channelType;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("instId")
  private String instrumentId;

  @Getter
  @AllArgsConstructor
  public enum InstType {
    SPOT("SPOT"),
    USDT_FUTURES("USDT-FUTURES"),
    COIN_FUTURES("COIN-FUTURES"),
    USDC_FUTURES("USDC-FUTURES");

    @JsonValue private final String value;

    public String toString() {
      return value;
    }
  }

  @Getter
  @AllArgsConstructor
  public enum ChannelType {
    TICKER("ticker"),

    DEPTH("books"),
    DEPTH1("books1"),
    DEPTH5("books5"),
    DEPTH15("books15"),

    FILL("fill"),

    POSITIONS("positions"),
    ACCOUNT("account");

    @JsonValue private final String value;

    public String toString() {
      return value;
    }
  }
}
