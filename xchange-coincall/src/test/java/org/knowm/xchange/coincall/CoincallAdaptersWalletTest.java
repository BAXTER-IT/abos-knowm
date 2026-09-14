package org.knowm.xchange.coincall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.knowm.xchange.coincall.dtos.account.CoincallAccountDto;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.Wallet.WalletFeature;

public class CoincallAdaptersWalletTest {

  @Test
  public void testToWallet_filtersNullAccountId_andMapsBalances() {
    CoincallAccountDto dto1 = new CoincallAccountDto();
    dto1.setAccountId(1L);
    dto1.setCoin("BTC");
    dto1.setAvailableBalance(new BigDecimal("0.5"));

    CoincallAccountDto dto2 = new CoincallAccountDto();
    dto2.setAccountId(null);     // should be filtered out
    dto2.setCoin("ETH");
    dto2.setAvailableBalance(new BigDecimal("2"));

    CoincallAccountDto dto3 = new CoincallAccountDto();
    dto3.setAccountId(2L);
    dto3.setCoin("USDT");
    dto3.setAvailableBalance(new BigDecimal("1000"));

    List<CoincallAccountDto> input = Arrays.asList(dto1, dto2, dto3);

    Wallet wallet = CoincallAdapters.toWallet(input);

    assertNotNull(wallet);

    Map<Currency, Balance> balances = wallet.getBalances();
    assertEquals(2, balances.size());

    Balance btc = balances.get(new Currency("BTC"));
    assertNotNull(btc);
    assertEquals(new BigDecimal("0.5"), btc.getAvailable());

    Balance usdt = balances.get(new Currency("USDT"));
    assertNotNull(usdt);
    assertEquals(new BigDecimal("1000"), usdt.getAvailable());

    // Features always included
    assertTrue(wallet.getFeatures().contains(WalletFeature.TRADING));
    assertTrue(wallet.getFeatures().contains(WalletFeature.MARGIN_TRADING));
    assertTrue(wallet.getFeatures().contains(WalletFeature.FUTURES_TRADING));
    assertTrue(wallet.getFeatures().contains(WalletFeature.OPTIONS_TRADING));
  }

  @Test
  public void testToWallet_singleValidAccount() {
    CoincallAccountDto dto = new CoincallAccountDto();
    dto.setAccountId(10L);
    dto.setCoin("SOL");
    dto.setAvailableBalance(new BigDecimal("25.7"));

    Wallet wallet = CoincallAdapters.toWallet(List.of(dto));

    assertNotNull(wallet);

    Map<Currency, Balance> balances = wallet.getBalances();
    assertEquals(1, balances.size());

    Balance sol = balances.get(new Currency("SOL"));
    assertNotNull(sol);
    assertEquals(new BigDecimal("25.7"), sol.getAvailable());

    // Feature set should not be empty
    assertFalse(wallet.getFeatures().isEmpty());
  }
}
