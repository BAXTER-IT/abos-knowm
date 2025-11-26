package info.bitrich.xchangestream.okex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.okex.dto.enums.OkexInstrumentType;

@Data
@AllArgsConstructor
public class OkexSubscribeMessage {

  private final String op;
  private final List<SubscriptionTopic> args;

  @Data
  @Builder
  @AllArgsConstructor
  public static class SubscriptionTopic {

    private final String channel;

    @JsonProperty("ccy")
    private final String currency;

    private final OkexInstrumentType instType;

    private final String instId;

    private final String extraParams;
  }
}
