package org.knowm.xchange.service.trade.params;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.TradeService;

/**
 * Generic {@link TradeHistoryParams} implementation that implements all the interfaces in the
 * hierarchy and can be safely (without getting exceptions, if that all the required fields are
 * non-null) passed to any implementation of {@link
 * TradeService#getTradeHistory(TradeHistoryParams)} .
 */
@Getter
@Setter
public class TradeHistoryParamsAll
    implements TradeHistoryParamsTimeSpan,
        TradeHistoryParamPaging,
        TradeHistoryParamsIdSpan,
        TradeHistoryParamOffset,
        TradeHistoryParamCurrencyPair,
        TradeHistoryParamMultiCurrencyPair,
        TradeHistoryParamInstrument,
        TradeHistoryParamMultiInstrument,
        TradeHistoryParamLimit,
        TradeHistoryParamId,
        TradeHistoryParamUserReference{

  private Integer pageLength;
  private Integer pageNumber;
  private String startId;
  private String endId;
  private Date startTime;
  private Date endTime;
  private Long offset;
  private Instrument instrument;
  private Collection<Instrument> instruments = Collections.emptySet();
  private Integer limit;
  private String id;
  private String userReference;
  private Map<String, Object> customParams = new HashMap<>();

  @Override
  public Long getOffset() {

    if (offset != null || pageLength == null || pageNumber == null) return offset;
    else return (long) pageLength * pageNumber;
  }

  @Override
  public CurrencyPair getCurrencyPair() {
    if (instrument instanceof CurrencyPair) {
      return (CurrencyPair) instrument;
    }

    return null;
  }

  @Override
  public void setCurrencyPair(CurrencyPair pair) {
    this.instrument = pair;
  }

  @Override
  public Collection<CurrencyPair> getCurrencyPairs() {
    if (!instruments.isEmpty()) {
      return instruments.stream()
          .filter(CurrencyPair.class::isInstance)
          .map(i -> (CurrencyPair) i)
          .collect(Collectors.toSet());
    }
    return Collections.emptySet();
  }

  @Override
  public void setCurrencyPairs(Collection<CurrencyPair> value) {
    this.instruments = new HashSet<>(value);
  }

  public Object getCustomParam(String key) {
    return customParams.get(key);
  }

  public void addCustomParam(String key, Object value) {
    customParams.put(key, value);
  }
}
