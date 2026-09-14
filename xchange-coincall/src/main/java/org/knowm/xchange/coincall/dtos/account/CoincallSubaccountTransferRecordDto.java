package org.knowm.xchange.coincall.dtos.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;
import org.knowm.xchange.coincall.dtos.enums.CoincallUserType;
import org.knowm.xchange.utils.jackson.RawJsonAware;

@Data
public class CoincallSubaccountTransferRecordDto implements RawJsonAware {

  @JsonProperty("id")
  private Long id;

  /**
   * Identifier of the main account that owns both source and destination users.
   */
  @JsonProperty("mainUserId")
  private Long mainUserId;

  @JsonProperty("sourceUserId")
  private Long sourceUserId;

  @JsonProperty("toUserId")
  private Long toUserId;

  @JsonProperty("sourceUserType")
  private CoincallUserType sourceUserType;

  @JsonProperty("toUserType")
  private CoincallUserType toUserType;

  @JsonProperty("sourceUserName")
  private String sourceUserName;

  @JsonProperty("toUserName")
  private String toUserName;

  @JsonProperty("amount")
  private BigDecimal amount;

  @JsonProperty("token")
  private String token;

  /**
   * Transfer creation time in milliseconds since Unix epoch.
   */
  @JsonProperty("createTime")
  private Long createTime;

  private String rawJson;
}