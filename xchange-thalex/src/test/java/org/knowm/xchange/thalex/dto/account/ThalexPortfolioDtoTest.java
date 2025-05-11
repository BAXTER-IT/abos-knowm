package org.knowm.xchange.thalex.dto.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.Test;

public class ThalexPortfolioDtoTest {

  @Test
  public void testEquals() {
    ThalexPortfolioDto dto1 = ThalexPortfolioDto.builder()
        .instrumentName("BTC-PERP")
        .position(new BigDecimal("1.5"))
        .markPrice(new BigDecimal("30000"))
        .impliedVolatility(new BigDecimal("0.6"))
        .index(new BigDecimal("29800"))
        .startPrice(new BigDecimal("29500"))
        .averagePrice(new BigDecimal("29600"))
        .unrealisedPnl(new BigDecimal("750"))
        .realisedPnl(new BigDecimal("1200"))
        .entryValue(new BigDecimal("44500"))
        .perpetualFundingEntryValue(new BigDecimal("100"))
        .unrealisedPerpetualFunding(new BigDecimal("50"))
        .build();

    ThalexPortfolioDto dto2 = ThalexPortfolioDto.builder()
        .instrumentName("BTC-PERP")
        .position(new BigDecimal("1.5"))
        .markPrice(new BigDecimal("30000"))
        .impliedVolatility(new BigDecimal("0.6"))
        .index(new BigDecimal("29800"))
        .startPrice(new BigDecimal("29500"))
        .averagePrice(new BigDecimal("29600"))
        .unrealisedPnl(new BigDecimal("750"))
        .realisedPnl(new BigDecimal("1200"))
        .entryValue(new BigDecimal("44500"))
        .perpetualFundingEntryValue(new BigDecimal("100"))
        .unrealisedPerpetualFunding(new BigDecimal("50"))
        .build();

    ThalexPortfolioDto dto3 = ThalexPortfolioDto.builder()
        .instrumentName("ETH-PERP") // different value
        .position(new BigDecimal("1.5"))
        .markPrice(new BigDecimal("30000"))
        .impliedVolatility(new BigDecimal("0.6"))
        .index(new BigDecimal("29800"))
        .startPrice(new BigDecimal("29500"))
        .averagePrice(new BigDecimal("29600"))
        .unrealisedPnl(new BigDecimal("750"))
        .realisedPnl(new BigDecimal("1200"))
        .entryValue(new BigDecimal("44500"))
        .perpetualFundingEntryValue(new BigDecimal("100"))
        .unrealisedPerpetualFunding(new BigDecimal("50"))
        .build();

    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    assertThat(dto1).isNotEqualTo(dto3);
  }

  @Test
  public void testToString() {
    ThalexPortfolioDto dto = ThalexPortfolioDto.builder()
        .instrumentName("BTC-PERP")
        .position(new BigDecimal("1.5"))
        .markPrice(new BigDecimal("30000"))
        .impliedVolatility(new BigDecimal("0.6"))
        .index(new BigDecimal("29800"))
        .startPrice(new BigDecimal("29500"))
        .averagePrice(new BigDecimal("29600"))
        .unrealisedPnl(new BigDecimal("750"))
        .realisedPnl(new BigDecimal("1200"))
        .entryValue(new BigDecimal("44500"))
        .perpetualFundingEntryValue(new BigDecimal("100"))
        .unrealisedPerpetualFunding(new BigDecimal("50"))
        .build();

    String toString = dto.toString();

    assertThat(toString).contains("instrumentName=BTC-PERP");
    assertThat(toString).contains("position=1.5");
    assertThat(toString).contains("markPrice=30000");
    assertThat(toString).contains("impliedVolatility=0.6");
    assertThat(toString).contains("index=29800");
    assertThat(toString).contains("startPrice=29500");
    assertThat(toString).contains("averagePrice=29600");
    assertThat(toString).contains("unrealisedPnl=750");
    assertThat(toString).contains("realisedPnl=1200");
    assertThat(toString).contains("entryValue=44500");
    assertThat(toString).contains("perpetualFundingEntryValue=100");
    assertThat(toString).contains("unrealisedPerpetualFunding=50");
  }
}