package org.knowm.xchange.kucoin.service;

import java.io.IOException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.knowm.xchange.kucoin.dto.response.KucoinResponse;
import org.knowm.xchange.kucoin.dto.response.TradeFeeResponse;

import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.SynchronizedValueFactory;

@Path("api/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface TradingFeeAPI {

  /**
   * Get basic fee rate of users.
   *
   * @return basic trading fee information
   */
  @GET
  @Path("/base-fee")
  KucoinResponse<TradeFeeResponse> getBaseFee(
      @HeaderParam(APIConstants.API_HEADER_KEY) String apiKey,
      @HeaderParam(APIConstants.API_HEADER_SIGN) ParamsDigest signature,
      @HeaderParam(APIConstants.API_HEADER_TIMESTAMP) SynchronizedValueFactory<Long> nonce,
      @HeaderParam(APIConstants.API_HEADER_PASSPHRASE) String apiPassphrase,
      @HeaderParam(APIConstants.API_HEADER_KEY_VERSION) String apiKeyVersion)
      throws IOException;

  @GET
  @Path("/trade-fees")
  KucoinResponse<List<TradeFeeResponse>> getTradeFee(
      @HeaderParam(APIConstants.API_HEADER_KEY) String apiKey,
      @HeaderParam(APIConstants.API_HEADER_SIGN) ParamsDigest signature,
      @HeaderParam(APIConstants.API_HEADER_TIMESTAMP) SynchronizedValueFactory<Long> nonce,
      @HeaderParam(APIConstants.API_HEADER_PASSPHRASE) String apiPassphrase,
      @HeaderParam(APIConstants.API_HEADER_KEY_VERSION) String apiKeyVersion,
      @QueryParam("symbols") String symbols)
      throws IOException;
}
