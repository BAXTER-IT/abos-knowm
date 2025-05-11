package org.knowm.xchange.thalex;

import java.io.IOException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.knowm.xchange.thalex.dto.ThalexResponse;
import org.knowm.xchange.thalex.dto.marketdata.ThalexInstrumentDto;
import org.knowm.xchange.thalex.exceptions.ThalexException;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface Thalex {

  @GET
  @Path("api/v2/public/instruments")
  ThalexResponse<List<ThalexInstrumentDto>> instruments() throws IOException, ThalexException;
}
