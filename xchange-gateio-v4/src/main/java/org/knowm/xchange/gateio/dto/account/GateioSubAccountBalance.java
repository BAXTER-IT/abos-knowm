package org.knowm.xchange.gateio.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.gateio.config.converter.EntryToBalanceDeserializer;

@Data
@Builder
@Jacksonized
public class GateioSubAccountBalance {

  @JsonProperty("uid")
  private String subAccountId;

  @JsonProperty("available")
  @JsonDeserialize(contentUsing = EntryToBalanceDeserializer.class)
  private List<Balance> available;
}
