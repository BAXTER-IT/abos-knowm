package org.knowm.xchange.gemini.dto.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.gemini.deserializers.GeminiTransactionTransferResponseDeserializer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = GeminiTransactionTransferResponseDeserializer.class)
public class GeminiTransactionTransferResponse implements GeminiTransaction {

  private Long timestamp;
  private String source;
  private String destination;
  private String operationReason;
  private String status;
  private Long eid;
  private String currency;
  private BigDecimal amount;
  private String method;
  private Long correlationId;
  private String transferType;
  private String bankId;
  private String purpose;
  private String transactionHash;
  private String transferId;
  private String withdrawalId;
  private String clientTransferId;
  private Long advanceEid;
  private Long pendingEid;
  private Long withdrawalEid;
  private String feeId;
  private String type;
  private String rawJson;
}
