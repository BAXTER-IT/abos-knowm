package info.bitrich.xchangestream.gemini.dto.trade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import info.bitrich.xchangestream.gemini.deserializers.GeminiWsTradeResponseDeserializer;
import info.bitrich.xchangestream.gemini.dto.enums.GeminiOrderEventType;
import info.bitrich.xchangestream.gemini.dto.enums.GeminiWsTradeSide;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = GeminiWsTradeResponseDeserializer.class)
public class GeminiWsTradeResponse {

  private GeminiOrderEventType type;
  private Long socketSequence;
  private String orderId;
  private String eventId;
  private String accountName;
  private String apiSession;
  private String clientOrderId;
  private String symbol;
  private GeminiWsTradeSide side;
  private String behavior;
  private String orderType;
  private String timestamp;
  private Long timestampms;
  private Boolean isLive;
  private Boolean isCancelled;
  private Boolean isHidden;
  private BigDecimal avgExecutionPrice;
  private BigDecimal executedAmount;
  private BigDecimal remainingAmount;
  private BigDecimal originalAmount;
  private BigDecimal price;
  private BigDecimal totalSpend;
  private GeminiWsTradeFillDto fill;
  private String rawJson;
}
