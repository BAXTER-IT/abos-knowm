package org.knowm.xchange.gemini.dto.trade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.gemini.deserializers.GeminiTransactionTradeResponseDeserializer;
import org.knowm.xchange.gemini.dto.account.GeminiTransaction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = GeminiTransactionTradeResponseDeserializer.class)
public class GeminiTransactionTradeResponse implements GeminiTransaction {

  private String account;
  private BigDecimal amount;
  private BigDecimal price;
  private Long timestampMs;
  private String side;
  private Boolean isAggressor;
  private String feeAssetCode;
  private BigDecimal feeAmount;
  private Long orderId;
  private String exchange;
  private Boolean isAuctionFill;
  private Boolean isClearingFill;
  private String symbol;
  private String type;
  private String rawJson;

}
