package org.knowm.xchange.derivative;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;

@Data
@Builder
public class CombinationsContract extends Instrument implements Derivative {

  private final CurrencyPair currencyPair;

  private final String instrumentName;

  private final Date expireDate;

  @Builder.Default private final List<Leg> legs = new ArrayList<>();

  @Override
  public Currency getBase() {
    return currencyPair.getBase();
  }

  @Override
  public Currency getCounter() {
    return currencyPair.getCounter();
  }

  @Data
  @Builder
  public static class Leg {
    private String instrumentName;
    private Integer quantity;
  }
}
