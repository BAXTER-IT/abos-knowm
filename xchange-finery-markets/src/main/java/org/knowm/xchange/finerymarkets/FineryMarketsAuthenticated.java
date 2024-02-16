package org.knowm.xchange.finerymarkets;

import static org.knowm.xchange.finerymarkets.service.FineryMarketsDigest.EFX_KEY;
import static org.knowm.xchange.finerymarkets.service.FineryMarketsDigest.EFX_SIGN;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.knowm.xchange.finerymarkets.dto.FineryMarketsPayload;
import org.knowm.xchange.finerymarkets.dto.marketdata.response.FineryMarketsInstrumentsResponse;
import si.mazi.rescu.ParamsDigest;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public interface FineryMarketsAuthenticated {

  @POST()
  @Path("instruments")
  FineryMarketsInstrumentsResponse getInstruments(
      @HeaderParam(EFX_KEY) String apiKey,
      @HeaderParam(EFX_SIGN) ParamsDigest signature,
      FineryMarketsPayload payload)
      throws FineryMarketsException;
}
