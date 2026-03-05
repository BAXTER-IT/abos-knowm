package info.bitrich.xchangestream.kucoin.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.kucoin.dto.KucoinWebSocketEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KucoinWsPositionsEvent extends KucoinWebSocketEvent {
    @JsonProperty("data")
    public KucoinWsPositionsData data;
}
