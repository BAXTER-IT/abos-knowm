package org.knowm.xchange.deribit.v2.dto.trade;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class DeribitUserTradeTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void keeps_the_contracts_field() throws Exception {
    DeribitUserTrade trade =
        mapper.readValue(
            "{\"trade_id\":\"446177648\",\"instrument_name\":\"BTC-25DEC26-70000-P\","
                + "\"amount\":0.1,\"contracts\":0.1,\"price\":0.015}",
            DeribitUserTrade.class);

    assertThat(trade.getContracts()).isEqualByComparingTo(new BigDecimal("0.1"));
    assertThat(trade.getAmount()).isEqualByComparingTo(new BigDecimal("0.1"));
  }

  @Test
  void contracts_is_null_when_deribit_leaves_it_out() throws Exception {
    // Deribit documents the field as optional, and it may be absent in historical trades
    DeribitUserTrade trade =
        mapper.readValue(
            "{\"trade_id\":\"446177648\",\"instrument_name\":\"BTC-25DEC26-70000-P\",\"amount\":0.1}",
            DeribitUserTrade.class);

    assertThat(trade.getContracts()).isNull();
  }
}
