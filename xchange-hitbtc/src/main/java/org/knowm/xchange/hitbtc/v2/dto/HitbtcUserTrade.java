package org.knowm.xchange.hitbtc.v2.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.knowm.xchange.dto.trade.UserTrade;

@Data
@SuperBuilder
public class HitbtcUserTrade extends UserTrade {

  private String clientOrderId;

}
