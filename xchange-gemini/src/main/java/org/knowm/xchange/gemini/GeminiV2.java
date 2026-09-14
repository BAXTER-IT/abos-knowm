package org.knowm.xchange.gemini;

import java.io.IOException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.knowm.xchange.gemini.dto.marketdata.GeminiCandle;
import org.knowm.xchange.gemini.dto.marketdata.GeminiTickerV2;
import org.knowm.xchange.gemini.exceptions.GeminiException;

@Path("v2")
@Produces(MediaType.APPLICATION_JSON)
public interface GeminiV2 {
  @GET
  @Path("candles/{symbol}/{time_frame}")
  GeminiCandle[] getCandles(
      @PathParam("symbol") String symbol, @PathParam("time_frame") String time_frame)
      throws IOException, GeminiException;

  @GET
  @Path("ticker/{symbol}")
  GeminiTickerV2 getTicker(@PathParam("symbol") String symbol) throws IOException, GeminiException;
}