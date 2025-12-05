package org.knowm.xchange.coincall;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import org.knowm.xchange.coincall.dtos.CoincallResponse;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallFuturesInstrumentDto;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallSpotInstrumentDto;
import org.knowm.xchange.coincall.exceptions.CoincallException;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface Coincall {

  @GET
  @Path("open/spot/market/instruments")
  CoincallResponse<List<CoincallSpotInstrumentDto>> spotInstruments()
      throws IOException, CoincallException;

  @GET
  @Path("open/futures/market/instruments/v1")
  CoincallResponse<List<CoincallFuturesInstrumentDto>> futuresInstruments()
      throws IOException, CoincallException;
}
