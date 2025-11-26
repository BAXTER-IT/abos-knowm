package info.bitrich.xchangestream.kucoin.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.kucoin.dto.enums.KucoinRelationEvent;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.currency.Currency;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class KucoinWsSpotBalanceData {

  @JsonProperty("accountId")
  private String accountId;

  /**
   * Currency, e.g. USDT.
   */
  @JsonProperty("currency")
  private Currency currency;

  /**
   * Total balance after the change.
   */
  @JsonProperty("total")
  private BigDecimal total;

  /**
   * Available balance after the change.
   */
  @JsonProperty("available")
  private BigDecimal available;

  /**
   * Hold (locked) balance after the change.
   */
  @JsonProperty("hold")
  private BigDecimal hold;

  /**
   * Change in available balance.
   */
  @JsonProperty("availableChange")
  private BigDecimal availableChange;

  /**
   * Change in hold (locked) balance.
   */
  @JsonProperty("holdChange")
  private BigDecimal holdChange;

  /**
   * Context of the related operation (symbol, order, trade).
   */
  @JsonProperty("relationContext")
  private KucoinSpotBalanceRelationContext relationContext;

  /**
   * Relation event type, e.g. main.deposit, trade.hold, isolated_BTC-USDT.hold, etc.
   */
  @JsonProperty("relationEvent")
  private KucoinRelationEvent relationEvent;

  /**
   * Relation event ID.
   */
  @JsonProperty("relationEventId")
  private String relationEventId;

  /**
   * Event time (Unix ms, as string).
   */
  @JsonProperty("time")
  private Long time;
}
