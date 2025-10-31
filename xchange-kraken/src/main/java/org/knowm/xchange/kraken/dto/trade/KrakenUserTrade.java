package org.knowm.xchange.kraken.dto.trade;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.knowm.xchange.dto.trade.UserTrade;

@Data
@SuperBuilder
public class KrakenUserTrade extends UserTrade {

  private BigDecimal cost;

}
