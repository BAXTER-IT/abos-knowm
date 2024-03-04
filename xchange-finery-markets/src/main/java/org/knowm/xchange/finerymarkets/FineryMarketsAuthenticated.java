package org.knowm.xchange.finerymarkets;

import static org.knowm.xchange.finerymarkets.service.FineryMarketsDigest.EFX_KEY;
import static org.knowm.xchange.finerymarkets.service.FineryMarketsDigest.EFX_SIGN;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
}
