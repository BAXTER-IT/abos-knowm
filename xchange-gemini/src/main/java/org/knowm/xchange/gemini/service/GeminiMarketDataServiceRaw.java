package org.knowm.xchange.gemini.service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.gemini.GeminiAdapters;
import org.knowm.xchange.gemini.GeminiExchange;
import org.knowm.xchange.gemini.GeminiUtils;
import org.knowm.xchange.gemini.dto.marketdata.GeminiCandle;
import org.knowm.xchange.gemini.dto.marketdata.GeminiDepth;
import org.knowm.xchange.gemini.dto.marketdata.GeminiLend;
import org.knowm.xchange.gemini.dto.marketdata.GeminiLendDepth;
import org.knowm.xchange.gemini.dto.marketdata.GeminiTicker;
import org.knowm.xchange.gemini.dto.marketdata.GeminiTickerV2;
import org.knowm.xchange.gemini.dto.marketdata.GeminiTrade;
import org.knowm.xchange.gemini.exceptions.GeminiException;

/**
 * Implementation of the market data service for Gemini
 *
 * <ul>
 *   <li>Provides access to various market data values
 * </ul>
 */
public class GeminiMarketDataServiceRaw extends GeminiBaseService {

  /**
   * Constructor
   *
   * @param exchange
   */
  public GeminiMarketDataServiceRaw(GeminiExchange exchange) {

    super(exchange);
  }

  public GeminiTicker getGeminiTicker(String pair) throws IOException {

    try {
      return geminiV1.getTicker(pair);
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public GeminiDepth getGeminiOrderBook(String pair, Integer limitBids, Integer limitAsks)
      throws IOException {

    try {
      GeminiDepth geminiDepth;
      if (limitBids == null && limitAsks == null) {
        geminiDepth = geminiV1.getBook(pair);
      } else {
        geminiDepth = geminiV1.getBook(pair, limitBids, limitAsks);
      }
      return geminiDepth;
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public GeminiLendDepth getGeminiLendBook(String currency, int limitBids, int limitAsks)
      throws IOException {

    try {
      return geminiV1.getLendBook(currency, limitBids, limitAsks);
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public GeminiTrade[] getGeminiTrades(String pair, long sinceTimestamp, int limitTrades)
      throws IOException {

    try {
      return geminiV1.getTrades(pair, sinceTimestamp, limitTrades);
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public GeminiLend[] getGeminiLends(String currency, long sinceTimestamp, int limitTrades)
      throws IOException {

    try {
      return geminiV1.getLends(currency, sinceTimestamp, limitTrades);
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public Collection<String> getGeminiSymbols() throws IOException {

    try {
      return geminiV1.getSymbols();
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public List<CurrencyPair> getExchangeSymbols() throws IOException {

    try {
      List<CurrencyPair> currencyPairs = new ArrayList<>();
      for (String symbol : geminiV1.getSymbols()) {
        currencyPairs.add(GeminiAdapters.adaptCurrencyPair(symbol));
      }
      return currencyPairs;
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public GeminiCandle[] getCandles(CurrencyPair pair, Duration interval) throws IOException {
    try {
      String timeFrame;
      if (interval.toDays() > 0) {
        timeFrame = interval.toDays() + "day";
      } else if (interval.toHours() > 0) {
        timeFrame = interval.toHours() + "hr";
      } else {
        timeFrame = interval.toMinutes() + "m";
      }

      return geminiV2.getCandles(GeminiUtils.toPairString(pair), timeFrame);
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public GeminiTickerV2 getTicker2(CurrencyPair pair) throws IOException {
    try {
      return geminiV2.getTicker(GeminiUtils.toPairString(pair));
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }
}
