package org.knowm.xchange.gateio;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.knowm.xchange.gateio.dto.GateioException;
import org.knowm.xchange.gateio.dto.marketdata.*;

import java.io.IOException;
import java.util.List;

@Path("api/v4")
@Produces(MediaType.APPLICATION_JSON)
public interface Gateio {

  @GET
  @Path("spot/currencies")
  List<GateioCurrencyInfo> getCurrencies() throws IOException, GateioException;


  @GET
  @Path("spot/order_book")
  GateioOrderBook getOrderBook(
      @QueryParam("currency_pair") String currencyPair,
      @QueryParam("with_id") Boolean withId
  ) throws IOException, GateioException;


  @GET
  @Path("wallet/currency_chains")
  List<GateioCurrencyChain> getCurrencyChains(@QueryParam("currency") String currency) throws IOException, GateioException;


  @GET
  @Path("spot/currency_pairs")
  List<GateioCurrencyPairDetails> getCurrencyPairDetails() throws IOException, GateioException;


  @GET
  @Path("spot/currency_pairs/{currency_pair}")
  GateioCurrencyPairDetails getCurrencyPairDetails(@PathParam("currency_pair") String currencyPair) throws IOException, GateioException;


  @GET
  @Path("spot/tickers")
  List<GateioTicker> getTickers(@QueryParam("currency_pair") String currencyPair) throws IOException, GateioException;


}
