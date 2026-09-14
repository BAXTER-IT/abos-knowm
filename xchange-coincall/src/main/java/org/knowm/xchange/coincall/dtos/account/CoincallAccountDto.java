package org.knowm.xchange.coincall.dtos.account;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;
import org.knowm.xchange.coincall.dtos.enums.CoincallMarginMode;

/**
 * Balance
 */
@Data
public class CoincallAccountDto {

  @JsonProperty("accountId")
  private Long accountId;

  @JsonProperty("coin")
  private String coin;

  @JsonProperty("coinIconUrl")
  private String coinIconUrl;

  @JsonProperty("coinDesc")
  private String coinDesc;

  @JsonProperty("coinView")
  private String coinView;

  @JsonProperty("equityAmount")
  private BigDecimal equityAmount;

  @JsonProperty("availableBalance")
  private BigDecimal availableBalance;

  @JsonProperty("cashBalanceAmount")
  private BigDecimal cashBalanceAmount;

  @JsonProperty("canWithdrawAmount")
  private BigDecimal canWithdrawAmount;

  @JsonProperty("marginMode")
  private CoincallMarginMode marginMode;

  @JsonProperty("marginBalance")
  private BigDecimal marginBalance;

  @JsonProperty("unrealizedAmount")
  private BigDecimal unrealizedAmount;

  @JsonProperty("imAmount")
  private BigDecimal imAmount;

  @JsonProperty("mmAmount")
  private BigDecimal mmAmount;

  @JsonProperty("delta")
  private BigDecimal delta;

  @JsonProperty("chainType")
  private String chainType;

  @JsonProperty("btcValue")
  private BigDecimal btcValue;

  @JsonProperty("dollarValue")
  private BigDecimal dollarValue;

  @JsonProperty("usdtValue")
  private BigDecimal usdtValue;

  /**
   * Number of decimal places supported for this asset.
   */
  @JsonProperty("decimal")
  private Integer decimal;
}