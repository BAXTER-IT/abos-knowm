package org.knowm.xchange.coincall.dtos.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;
import org.knowm.xchange.coincall.dtos.enums.CoincallTransactionSide;
import org.knowm.xchange.coincall.dtos.enums.CoincallTransactionStatus;
import org.knowm.xchange.coincall.dtos.enums.CoincallTransactionType;
import org.knowm.xchange.utils.jackson.RawJsonAware;

@Data
public class CoincallTransactionDto implements RawJsonAware {

  @JsonProperty("transactionRecordId")
  private Long transactionRecordId;

  @JsonProperty("createTime")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
  private Instant createTime;

  @JsonProperty("side")
  private CoincallTransactionSide side;

  @JsonProperty("coin")
  private String coin;

  @JsonProperty("amount")
  private BigDecimal amount;

  @JsonProperty("serviceFee")
  private BigDecimal serviceFee;

  @JsonProperty("address")
  private String address;

  @JsonProperty("network")
  private String network;

  /**
   * Explorer URL for the address.
   */
  @JsonProperty("addressUrl")
  private String addressUrl;

  @JsonProperty("txId")
  private String txId;

  /**
   * Explorer URL for the transaction hash.
   */
  @JsonProperty("txIdUrl")
  private String txIdUrl;

  @JsonProperty("status")
  private CoincallTransactionStatus status;

  /**
   * Minimum required confirmations for the blockchain network.
   */
  @JsonProperty("confirmingThreshold")
  private Integer confirmingThreshold;

  /**
   * Number of confirmations already achieved.
   */
  @JsonProperty("confirmedNum")
  private Integer confirmedNum;

  @JsonProperty("type")
  private CoincallTransactionType type;

  private String rawJson;
}