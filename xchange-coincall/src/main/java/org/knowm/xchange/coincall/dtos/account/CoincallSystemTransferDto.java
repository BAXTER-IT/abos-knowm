package org.knowm.xchange.coincall.dtos.account;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;
import org.knowm.xchange.coincall.dtos.enums.CoincallSystemTransferSide;
import org.knowm.xchange.coincall.dtos.enums.CoincallSystemTransferState;
import org.knowm.xchange.coincall.dtos.enums.CoincallSystemTransferType;
import org.knowm.xchange.utils.jackson.RawJsonAware;

@Data
public class CoincallSystemTransferDto implements RawJsonAware {

  /**
   * Event timestamp in milliseconds since the Unix epoch.
   */
  @JsonProperty("time")
  private Long time;

  /**
   * Business type of the transfer.
   */
  @JsonProperty("type")
  private CoincallSystemTransferType type;

  /**
   * Direction of balance change.
   */
  @JsonProperty("side")
  private CoincallSystemTransferSide side;

  /**
   * Absolute amount of the balance change.
   */
  @JsonProperty("creditChange")
  private BigDecimal creditChange;

  @JsonProperty("txId")
  private String txId;

  @JsonProperty("state")
  private CoincallSystemTransferState state;

  @JsonProperty("coin")
  private String coin;

  @JsonProperty("note")
  private String note;

  @JsonProperty("userId")
  private String userId;

  private String rawJson;
}