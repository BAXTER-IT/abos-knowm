package org.knowm.xchange.thalex.config.deserialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.thalex.dto.account.ThalexTransactionDto;
import org.knowm.xchange.thalex.dto.enums.ThalexTransactionType;

public class ThalexTransactionDtoDeserializerTest {

  @Test
  public void testDeserialize_perpetualFunding() throws IOException {
    String json = "{ \"asset\": \"USDT\", \"time\": 1.747728000010952E9, \"amount\": 0.0, " +
        "\"description\": \"perpetual funding USD 0.00000000 @ 0.99966921\", " +
        "\"transaction_type\": \"perpetual funding\", " +
        "\"instrument_name\": \"BTC-PERPETUAL\", \"balance_after\": 100027.7057691 }";

    ThalexTransactionDto dto = deserializeTransaction(json);

    assertNotNull(dto);
    assertEquals(new Currency("USDT"), dto.getAsset());
    assertEquals(new BigDecimal("0.0"), dto.getAmount());
    assertEquals("perpetual funding USD 0.00000000 @ 0.99966921", dto.getDescription());
    assertEquals(ThalexTransactionType.PERPETUAL_FUNDING, dto.getTransactionType());
    assertEquals("BTC-PERPETUAL", dto.getInstrumentName());
    assertEquals(new BigDecimal("100027.7057691"), dto.getBalanceAfter());
    assertNotNull(dto.getTime());
    assertNotNull(dto.getRawJson());
  }

  @Test
  public void testDeserialize_sessionSettlement() throws IOException {
    String json = "{ \"asset\": \"USDT\", \"time\": 1.747728000010952E9, \"amount\": 27.7057691, " +
        "\"description\": \"daily settlement USD 27.69660431 (pos 32.90660431, fees 5.21000000) @ 0.99966921\", "
        +
        "\"transaction_type\": \"session settlement\", " +
        "\"instrument_name\": \"BTC-PERPETUAL\", \"balance_after\": 100027.7057691 }";

    ThalexTransactionDto dto = deserializeTransaction(json);

    assertNotNull(dto);
    assertEquals(new Currency("USDT"), dto.getAsset());
    assertEquals(new BigDecimal("27.7057691"), dto.getAmount());
    assertEquals(ThalexTransactionType.SESSION_SETTLEMENT, dto.getTransactionType());
    assertEquals("BTC-PERPETUAL", dto.getInstrumentName());
    assertEquals("daily settlement USD 27.69660431 (pos 32.90660431, fees 5.21000000) @ 0.99966921",
        dto.getDescription());
    assertEquals(new BigDecimal("100027.7057691"), dto.getBalanceAfter());
    assertNotNull(dto.getTime());
    assertNotNull(dto.getRawJson());
  }

  @Test
  public void testDeserialize_deposit() throws IOException {
    String json = "{ \"asset\": \"ETH\", \"time\": 1.7476825164448211E9, \"amount\": 10.0, " +
        "\"description\": \"chain=testnet tx=xx_fake_8d5a2ef7683b7dc5d1c0d46a52ac3f40\", " +
        "\"transaction_type\": \"deposit\", \"balance_after\": 10.0 }";

    ThalexTransactionDto dto = deserializeTransaction(json);

    assertNotNull(dto);
    assertEquals(new Currency("ETH"), dto.getAsset());
    assertEquals(new BigDecimal("10.0"), dto.getAmount());
    assertEquals(ThalexTransactionType.DEPOSIT, dto.getTransactionType());
    assertEquals("chain=testnet tx=xx_fake_8d5a2ef7683b7dc5d1c0d46a52ac3f40", dto.getDescription());
    assertNull(dto.getInstrumentName());
    assertEquals(new BigDecimal("10.0"), dto.getBalanceAfter());
    assertNotNull(dto.getTime());
  }

  @Test
  public void testDeserialize_withdrawal() throws IOException {
    String json = "{ \"asset\": \"USDT\", \"time\": 1.7476824515778701E9, \"amount\": -1000.0, " +
        "\"description\": \"withdrawal 0xdac17f958d2ee523a2206206994597c13d831ec7 <no_label>\", " +
        "\"transaction_type\": \"withdrawal\", \"balance_after\": 99000.0 }";

    ThalexTransactionDto dto = deserializeTransaction(json);

    assertNotNull(dto);
    assertEquals(new Currency("USDT"), dto.getAsset());
    assertEquals(new BigDecimal("-1000.0"), dto.getAmount());
    assertEquals(ThalexTransactionType.WITHDRAWAL, dto.getTransactionType());
    assertEquals("withdrawal 0xdac17f958d2ee523a2206206994597c13d831ec7 <no_label>",
        dto.getDescription());
    assertEquals(new BigDecimal("99000.0"), dto.getBalanceAfter());
    assertNull(dto.getInstrumentName());
    assertNotNull(dto.getTime());
  }

  @Test
  public void testDeserialize_withdrawalFee() throws IOException {
    String json = "{ \"asset\": \"USDT\", \"time\": 1.7476824515778701E9, \"amount\": -25.0, " +
        "\"description\": \"withdrawal fee <no label>\", " +
        "\"transaction_type\": \"withdrawal fee\", \"balance_after\": 98975.0 }";

    ThalexTransactionDto dto = deserializeTransaction(json);

    assertNotNull(dto);
    assertEquals(new Currency("USDT"), dto.getAsset());
    assertEquals(new BigDecimal("-25.0"), dto.getAmount());
    assertEquals(ThalexTransactionType.WITHDRAWAL_FEE, dto.getTransactionType());
    assertEquals("withdrawal fee <no label>", dto.getDescription());
    assertEquals(new BigDecimal("98975.0"), dto.getBalanceAfter());
    assertNull(dto.getInstrumentName());
    assertNotNull(dto.getTime());
  }

  private ThalexTransactionDto deserializeTransaction(String json) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(ThalexTransactionDto.class, new ThalexTransactionDtoDeserializer());
    mapper.registerModule(module);
    return mapper.readValue(json, ThalexTransactionDto.class);
  }

}
