package org.knowm.xchange.finerymarkets;

import static org.knowm.xchange.finerymarkets.service.FineryMarketsDigest.EFX_KEY;
import static org.knowm.xchange.finerymarkets.service.FineryMarketsDigest.EFX_SIGN;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.knowm.xchange.finerymarkets.dto.DecoratedPayload;
import org.knowm.xchange.finerymarkets.dto.marketdata.response.InstrumentsResponse;
import org.knowm.xchange.finerymarkets.dto.trade.response.DealHistoryResponse;
import si.mazi.rescu.ParamsDigest;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface FineryMarketsAuthenticated {

  @POST()
  @Path("instruments")
  InstrumentsResponse getInstruments(
      @HeaderParam(EFX_KEY) String apiKey,
      @HeaderParam(EFX_SIGN) ParamsDigest signature,
      DecoratedPayload payload)
      throws FineryMarketsException;

  @POST()
  @Path("dealHistory")
  DealHistoryResponse getDealHistory(
      @HeaderParam(EFX_KEY) String apiKey,
      @HeaderParam(EFX_SIGN) ParamsDigest signature,
      DecoratedPayload payload)
      throws FineryMarketsException;

  @POST()
  @Path("disableTrading")
  void disableTrading(
      @HeaderParam(EFX_KEY) String apiKey,
      @HeaderParam(EFX_SIGN) ParamsDigest signature,
      DecoratedPayload payload)
      throws FineryMarketsException;

  @POST()
  @Path("enableTrading")
  void enableTrading(
      @HeaderParam(EFX_KEY) String apiKey,
      @HeaderParam(EFX_SIGN) ParamsDigest signature,
      DecoratedPayload payload)
      throws FineryMarketsException;
}
