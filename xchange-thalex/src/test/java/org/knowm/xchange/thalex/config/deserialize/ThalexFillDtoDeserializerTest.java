package org.knowm.xchange.thalex.config.deserialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.math.BigDecimal;
import org.junit.Test;
import org.knowm.xchange.thalex.dto.enums.ThalexDirection;
import org.knowm.xchange.thalex.dto.enums.ThalexMakerTaker;
import org.knowm.xchange.thalex.dto.enums.ThalexTradeType;
import org.knowm.xchange.thalex.dto.trade.ThalexTradeDto;

public class ThalexFillDtoDeserializerTest {

  @Test
  public void testDeserializeThalexTradeDto() throws Exception {
    String json = "{\n" +
        "  \"order_id\": \"001F3C2600000019\",\n" +
        "  \"trade_id\": \"008000000000000D18425435FE7D2FD5\",\n" +
        "  \"instrument_name\": \"BTC-PERPETUAL\",\n" +
        "  \"direction\": \"buy\",\n" +
        "  \"price\": 107454.0,\n" +
        "  \"amount\": 0.025,\n" +
        "  \"time\": 1748052196.2527912,\n" +
        "  \"position_after\": 1.1,\n" +
        "  \"trade_type\": \"normal\",\n" +
        "  \"session_realised_after\": -1181.379625,\n" +
        "  \"fee\": 0.67,\n" +
        "  \"index\": 107428.04,\n" +
        "  \"fee_rate\": 0.00025,\n" +
        "  \"funding_mark\": 0.0,\n" +
        "  \"maker_taker\": \"taker\",\n" +
        "  \"leg_index\": 0\n" +
        "}";

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(ThalexTradeDto.class, new ThalexFillDtoDeserializer());
    mapper.registerModule(module);

    ThalexTradeDto dto = mapper.readValue(json, ThalexTradeDto.class);

    assertNotNull(dto);
    assertEquals("001F3C2600000019", dto.getOrderId());
    assertEquals("008000000000000D18425435FE7D2FD5", dto.getTradeId());
    assertEquals("BTC-PERPETUAL", dto.getInstrumentName());
    assertEquals(ThalexDirection.BUY, dto.getDirection());
    assertEquals(new BigDecimal("107454.0"), dto.getPrice());
    assertEquals(new BigDecimal("0.025"), dto.getAmount());
    assertEquals(new BigDecimal("1.1"), dto.getPositionAfter());
    assertEquals(ThalexTradeType.NORMAL, dto.getTradeType());
    assertEquals(new BigDecimal("-1181.379625"), dto.getSessionRealisedAfter());
    assertEquals(new BigDecimal("0.67"), dto.getFee());
    assertEquals(new BigDecimal("107428.04"), dto.getIndex());
    assertEquals(new BigDecimal("0.00025"), dto.getFeeRate());
    assertEquals(new BigDecimal("0.0"), dto.getFundingMark());
    assertEquals(ThalexMakerTaker.TAKER, dto.getMakerTaker());
    assertNotNull(dto.getTime());
    assertNotNull(dto.getRawJson());
  }

  @Test
  public void testDeserialize_missingOptionalFields() throws Exception {
    String json = "{\n" +
        "  \"order_id\": \"001F3C2600000019\",\n" +
        "  \"trade_id\": \"008000000000000D18425435FE7D2FD5\",\n" +
        "  \"instrument_name\": \"BTC-PERPETUAL\",\n" +
        "  \"direction\": \"buy\",\n" +
        "  \"price\": 107454.0,\n" +
        "  \"amount\": 0.025,\n" +
        "  \"time\": 1748052196.2527912,\n" +
        "  \"maker_taker\": \"taker\",\n" +
        "  \"trade_type\": \"normal\"\n" +
        "}";

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(ThalexTradeDto.class, new ThalexFillDtoDeserializer());
    mapper.registerModule(module);

    ThalexTradeDto dto = mapper.readValue(json, ThalexTradeDto.class);

    assertNotNull(dto);
    assertEquals("001F3C2600000019", dto.getOrderId());
    assertNull(dto.getLabel());
    assertNull(dto.getClientOrderId());
  }

  @Test
  public void testDeserialize_nullEnums() throws Exception {
    String json = "{\n" +
        "  \"order_id\": \"001\",\n" +
        "  \"trade_id\": \"002\",\n" +
        "  \"instrument_name\": \"BTC-PERPETUAL\",\n" +
        "  \"price\": 10000,\n" +
        "  \"amount\": 0.1,\n" +
        "  \"time\": 1748052000.0,\n" +
        "  \"trade_type\": \"normal\"\n" +
        "}";

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(ThalexTradeDto.class, new ThalexFillDtoDeserializer());
    mapper.registerModule(module);

    ThalexTradeDto dto = mapper.readValue(json, ThalexTradeDto.class);

    assertNotNull(dto);
    assertNull(dto.getDirection());
    assertNull(dto.getMakerTaker());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeserialize_invalidDirectionValue() throws Exception {
    String json = "{\n" +
        "  \"direction\": \"invalid_enum\",\n" +
        "  \"trade_type\": \"normal\",\n" +
        "  \"order_id\": \"001\",\n" +
        "  \"trade_id\": \"002\",\n" +
        "  \"instrument_name\": \"BTC-PERPETUAL\",\n" +
        "  \"price\": 10000,\n" +
        "  \"amount\": 0.1,\n" +
        "  \"time\": 1748052000.0\n" +
        "}";

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(ThalexTradeDto.class, new ThalexFillDtoDeserializer());
    mapper.registerModule(module);

    mapper.readValue(json, ThalexTradeDto.class);
  }

  @Test
  public void testDeserialize_zeroAndNegativeValues() throws Exception {
    String json = "{\n" +
        "  \"order_id\": \"001\",\n" +
        "  \"trade_id\": \"002\",\n" +
        "  \"instrument_name\": \"BTC-PERPETUAL\",\n" +
        "  \"direction\": \"sell\",\n" +
        "  \"price\": 0,\n" +
        "  \"amount\": -0.01,\n" +
        "  \"time\": 1748052000.0,\n" +
        "  \"trade_type\": \"normal\"\n" +
        "}";

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(ThalexTradeDto.class, new ThalexFillDtoDeserializer());
    mapper.registerModule(module);

    ThalexTradeDto dto = mapper.readValue(json, ThalexTradeDto.class);

    assertNotNull(dto);
    assertEquals(new BigDecimal("0"), dto.getPrice());
    assertEquals(new BigDecimal("-0.01"), dto.getAmount());
    assertEquals(ThalexDirection.SELL, dto.getDirection());
  }


}