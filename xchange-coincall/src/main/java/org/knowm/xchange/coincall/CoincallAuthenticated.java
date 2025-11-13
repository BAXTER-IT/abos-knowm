package org.knowm.xchange.coincall;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import org.knowm.xchange.coincall.dtos.CoincallPagedResponse;
import org.knowm.xchange.coincall.dtos.CoincallPaginatedResponse;
import org.knowm.xchange.coincall.dtos.CoincallResponse;
import org.knowm.xchange.coincall.dtos.account.CoincallAccountSummaryDto;
import org.knowm.xchange.coincall.dtos.account.CoincallFuturesPositionDto;
import org.knowm.xchange.coincall.dtos.account.CoincallSubaccountTransferRecordDto;
import org.knowm.xchange.coincall.dtos.account.CoincallSystemTransferDto;
import org.knowm.xchange.coincall.dtos.account.CoincallTransactionDto;
import org.knowm.xchange.coincall.dtos.trade.CoincallFuturesTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallOptionTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallSpotFillDto;
import org.knowm.xchange.coincall.exceptions.CoincallException;
import si.mazi.rescu.ParamsDigest;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface CoincallAuthenticated {

  @GET
  @Path("open/spot/trade/fills/v1")
  CoincallResponse<List<CoincallSpotFillDto>> spotFills(
      @HeaderParam("sign") ParamsDigest sign,
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") Long orderId,
      @QueryParam("limit") Integer limit,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime
  ) throws IOException, CoincallException;

  @GET
  @Path("open/futures/trade/history/v1")
  CoincallResponse<CoincallPaginatedResponse<CoincallFuturesTransactionDetail>> futuresTransactionDetails(
      @HeaderParam("sign") ParamsDigest sign,
      @QueryParam("pageSize") Integer pageSize,
      @QueryParam("fromId") Long fromId,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime
  ) throws IOException, CoincallException;

  @GET
  @Path("open/option/trade/history/v1")
  CoincallResponse<CoincallPaginatedResponse<CoincallOptionTransactionDetail>> optionTransactionDetails(
      @HeaderParam("sign") ParamsDigest sign,
      @QueryParam("pageSize") Integer pageSize,
      @QueryParam("fromId") Long fromId,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime
  ) throws IOException, CoincallException;

  @GET
  @Path("open/account/summary/v1")
  CoincallResponse<CoincallAccountSummaryDto> accountSummary(
      @HeaderParam("sign") ParamsDigest sign
  ) throws IOException, CoincallException;

  @GET
  @Path("open/futures/position/get/v1")
  CoincallResponse<List<CoincallFuturesPositionDto>> futuresPositions(
      @HeaderParam("sign") ParamsDigest sign
  ) throws IOException, CoincallException;

  @GET
  @Path("open/account/historyList/v1")
  CoincallResponse<CoincallPagedResponse<CoincallTransactionDto>> transactionHistory(
      @HeaderParam("sign") ParamsDigest sign,
      @QueryParam("type") Integer type,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("page") Integer page,
      @QueryParam("pageSize") Integer pageSize)
      throws IOException, CoincallException;

  @GET
  @Path("open/account/sysTransferRecords/v1")
  CoincallResponse<CoincallPagedResponse<CoincallSystemTransferDto>> systemTransferRecords(
      @HeaderParam("sign") ParamsDigest sign
  ) throws IOException, CoincallException;

  @GET
  @Path("open/user/subAccount/transfer/records/v1")
  CoincallResponse<CoincallPagedResponse<CoincallSubaccountTransferRecordDto>> subaccountTransferRecords(
      @HeaderParam("sign") ParamsDigest sign,
      @QueryParam("page") Integer page,
      @QueryParam("pageSize") Integer pageSize
  ) throws IOException, CoincallException;
}
