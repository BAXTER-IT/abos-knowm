package info.bitrich.xchangestream.bitget.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import info.bitrich.xchangestream.bitget.dto.response.BitgetWsPositionNotification.BitgetPosition;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@SuperBuilder(toBuilder = true)
@Jacksonized
public class BitgetWsPositionNotification extends BitgetWsNotification<BitgetPosition> {

  @Getter
  @RequiredArgsConstructor
  public enum BitgetAssetMode {

    UNION("union"),
    SINGLE("single");

    @JsonValue
    private final String code;

    @JsonCreator
    public static BitgetAssetMode fromCode(String code) {
      for (BitgetAssetMode value : values()) {
        if (value.code.equalsIgnoreCase(code)) {
          return value;
        }
      }
      throw new IllegalArgumentException("Unknown BitgetAssetMode code: " + code);
    }
  }

  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BitgetPosition {

    /**
     * Position ID.
     */
    @JsonProperty("posId")
    private String positionId;

    /**
     * Product / instrument ID.
     */
    @JsonProperty("instId")
    private String instrumentId;

    /**
     * Currency of occupied margin.
     */
    @JsonProperty("marginCoin")
    private String marginCoin;

    /**
     * Occupied margin amount.
     */
    @JsonProperty("marginSize")
    private BigDecimal marginSize;

    /**
     * Margin mode.
     */
    @JsonProperty("marginMode")
    private String marginMode;

    /**
     * Position direction (hold side).
     */
    @JsonProperty("holdSide")
    private String holdSide;

    /**
     * Position mode.
     */
    @JsonProperty("posMode")
    private String positionMode;

    /**
     * Total open position size.
     */
    @JsonProperty("total")
    private BigDecimal totalSize;

    /**
     * Size of positions that can be closed.
     */
    @JsonProperty("available")
    private BigDecimal availableSize;

    /**
     * Amount of frozen margin.
     */
    @JsonProperty("frozen")
    private BigDecimal frozenMargin;

    /**
     * Average entry price.
     */
    @JsonProperty("openPriceAvg")
    private BigDecimal averageOpenPrice;

    /**
     * Leverage.
     */
    @JsonProperty("leverage")
    private BigDecimal leverage;

    /**
     * Realized PnL.
     */
    @JsonProperty("achievedProfits")
    private BigDecimal realizedProfits;

    /**
     * Unrealized PnL.
     */
    @JsonProperty("unrealizedPL")
    private BigDecimal unrealizedProfitAndLoss;

    /**
     * Unrealized ROI.
     */
    @JsonProperty("unrealizedPLR")
    private BigDecimal unrealizedReturnOnInvestment;

    /**
     * Estimated liquidation price.
     */
    @JsonProperty("liquidationPrice")
    private BigDecimal liquidationPrice;

    /**
     * Maintenance margin rate.
     */
    @JsonProperty("keepMarginRate")
    private BigDecimal maintenanceMarginRate;

    /**
     * Actual margin ratio under isolated margin mode.
     */
    @JsonProperty("isolatedMarginRate")
    private BigDecimal isolatedMarginRate;

    /**
     * Occupancy rate of margin.
     */
    @JsonProperty("marginRate")
    private BigDecimal marginRate;

    /**
     * Position breakeven price.
     */
    @JsonProperty("breakEvenPrice")
    private BigDecimal breakEvenPrice;

    /**
     * Accumulated funding fee for the position.
     */
    @JsonProperty("totalFee")
    private BigDecimal totalFundingFee;

    /**
     * Deducted transaction fees during the position.
     */
    @JsonProperty("deductedFee")
    private BigDecimal deductedTransactionFees;

    /**
     * Mark price.
     */
    @JsonProperty("markPrice")
    private BigDecimal markPrice;

    /**
     * Account mode (union margin or single margin).
     */
    @JsonProperty("assetMode")
    private BitgetAssetMode assetMode;

    /**
     * Position creation time (Unix ms).
     */
    @JsonProperty("cTime")
    private String creationTime;

    /**
     * Latest position update time (Unix ms).
     */
    @JsonProperty("uTime")
    private String updateTime;
  }

}
