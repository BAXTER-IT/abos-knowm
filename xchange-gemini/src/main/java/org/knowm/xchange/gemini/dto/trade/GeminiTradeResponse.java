package org.knowm.xchange.gemini.dto.trade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.gemini.deserializers.GeminiTradeResponseDeserializer;
import org.knowm.xchange.gemini.dto.enums.GeminiTradeBreak;
import org.knowm.xchange.gemini.dto.enums.GeminiTradeType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = GeminiTradeResponseDeserializer.class)
public class GeminiTradeResponse {

  private BigDecimal price;
  private BigDecimal amount;
  private String symbol;
  private Long timestamp;
  private Long timestampms;
  private GeminiTradeType type;
  private Boolean aggressor;
  private String feeCurrency;
  private BigDecimal feeAmount;
  private Integer tradeId;
  private String orderId;
  private String clientOrderId;
  private String exchange;
  private Boolean isAuctionFill;
  private GeminiTradeBreak breakValue;
  private String rawJson;

}
