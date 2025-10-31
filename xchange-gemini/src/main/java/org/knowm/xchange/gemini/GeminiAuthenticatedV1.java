package org.knowm.xchange.gemini;

import java.io.IOException;
import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.knowm.xchange.gemini.dto.account.GeminiBalancesRequest;
import org.knowm.xchange.gemini.dto.account.GeminiBalancesResponse;
import org.knowm.xchange.gemini.dto.account.GeminiDepositAddressRequest;
import org.knowm.xchange.gemini.dto.account.GeminiDepositAddressResponse;
import org.knowm.xchange.gemini.dto.account.GeminiPositionsRequest;
import org.knowm.xchange.gemini.dto.account.GeminiPositionsResponse;
import org.knowm.xchange.gemini.dto.account.GeminiTrailingVolumeRequest;
import org.knowm.xchange.gemini.dto.account.GeminiTrailingVolumeResponse;
import org.knowm.xchange.gemini.dto.account.GeminiTransactionRequest;
import org.knowm.xchange.gemini.dto.account.GeminiTransactionResponse;
import org.knowm.xchange.gemini.dto.account.GeminiTransferRequest;
import org.knowm.xchange.gemini.dto.account.GeminiTransferResponse;
import org.knowm.xchange.gemini.dto.account.GeminiWithdrawalRequest;
import org.knowm.xchange.gemini.dto.account.GeminiWithdrawalResponse;
import org.knowm.xchange.gemini.dto.trade.GeminiCancelAllOrdersRequest;
import org.knowm.xchange.gemini.dto.trade.GeminiCancelAllOrdersResponse;
import org.knowm.xchange.gemini.dto.trade.GeminiCancelOrderRequest;
import org.knowm.xchange.gemini.dto.trade.GeminiNewOrderRequest;
import org.knowm.xchange.gemini.dto.trade.GeminiNonceOnlyRequest;
import org.knowm.xchange.gemini.dto.trade.GeminiOrderStatusRequest;
import org.knowm.xchange.gemini.dto.trade.GeminiOrderStatusResponse;
import org.knowm.xchange.gemini.dto.trade.GeminiPastTradesRequest;
import org.knowm.xchange.gemini.dto.trade.GeminiTradeResponse;
import org.knowm.xchange.gemini.exceptions.GeminiException;
import si.mazi.rescu.ParamsDigest;

@Path("v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeminiAuthenticatedV1 {

  String HEADER_PARAM_APIKEY = "X-GEMINI-APIKEY";
  String HEADER_PARAM_PAYLOAD = "X-GEMINI-PAYLOAD";
  String HEADER_PARAM_SIGNATURE = "X-GEMINI-SIGNATURE";

  @POST
  @Path("order/new")
  GeminiOrderStatusResponse newOrder(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      GeminiNewOrderRequest newOrderRequest)
      throws IOException, GeminiException;

  @POST
  @Path("balances")
  List<GeminiBalancesResponse> balances(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      GeminiBalancesRequest balancesRequest)
      throws IOException, GeminiException;

  @POST
  @Path("positions")
  List<GeminiPositionsResponse> positions(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      GeminiPositionsRequest request)
      throws IOException, GeminiException;

  @POST
  @Path("order/cancel")
  GeminiOrderStatusResponse cancelOrders(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      GeminiCancelOrderRequest cancelOrderRequest)
      throws IOException, GeminiException;

  @POST
  @Path("order/cancel/session")
  GeminiCancelAllOrdersResponse cancelAllSessionOrders(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      GeminiCancelAllOrdersRequest cancelAllOrdersRequest)
      throws IOException, GeminiException;

  @POST
  @Path("order/cancel/all")
  GeminiCancelAllOrdersResponse cancelAllOrders(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      GeminiCancelAllOrdersRequest cancelAllOrdersRequest)
      throws IOException, GeminiException;

  @POST
  @Path("orders")
  GeminiOrderStatusResponse[] activeOrders(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      GeminiNonceOnlyRequest nonceOnlyRequest)
      throws IOException, GeminiException;

  @POST
  @Path("order/status")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  GeminiOrderStatusResponse orderStatus(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      GeminiOrderStatusRequest orderStatusRequest)
      throws IOException, GeminiException;

  @POST
  @Path("mytrades")
  List<GeminiTradeResponse> pastTrades(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      GeminiPastTradesRequest pastTradesRequest)
      throws IOException, GeminiException;

  @POST
  @Path("notionalvolume")
  GeminiTrailingVolumeResponse TrailingVolume(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      GeminiTrailingVolumeRequest pastTradesRequest)
      throws IOException, GeminiException;

  @POST
  @Path("withdraw/{currency}")
  GeminiWithdrawalResponse withdraw(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      @PathParam("currency") String currency,
      GeminiWithdrawalRequest withdrawalRequest)
      throws IOException, GeminiException;

  @POST
  @Path("deposit/{currency}/newAddress")
  GeminiDepositAddressResponse requestNewAddress(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payload,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signature,
      @PathParam("currency") String currency,
      GeminiDepositAddressRequest depositRequest)
      throws IOException, GeminiException;

  @POST
  @Path("transfers")
  List<GeminiTransferResponse> transfers(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payloadCreator,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signatureCreator,
      GeminiTransferRequest request)
      throws IOException, GeminiException;

  @POST
  @Path("transactions")
  GeminiTransactionResponse transactions(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payloadCreator,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signatureCreator,
      GeminiTransactionRequest request)
      throws IOException, GeminiException;

  @POST
  @Path("heartbeat")
  GeminiOrderStatusResponse heartBeat(
      @HeaderParam(HEADER_PARAM_APIKEY) String apiKey,
      @HeaderParam(HEADER_PARAM_PAYLOAD) ParamsDigest payloadCreator,
      @HeaderParam(HEADER_PARAM_SIGNATURE) ParamsDigest signatureCreator,
      GeminiNonceOnlyRequest nonceOnlyRequest)
      throws IOException, GeminiException;
}