package org.knowm.xchange.thalex;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import org.knowm.xchange.thalex.dto.ThalexResponse;
import org.knowm.xchange.thalex.dto.account.ThalexAccountSummaryDto;
import org.knowm.xchange.thalex.dto.account.ThalexCryptoDeposits;
import org.knowm.xchange.thalex.dto.account.ThalexDailyMarkHistoryResult;
import org.knowm.xchange.thalex.dto.account.ThalexPortfolioDto;
import org.knowm.xchange.thalex.dto.account.ThalexTransactionsDto;
import org.knowm.xchange.thalex.dto.account.ThalexWithdrawal;
import org.knowm.xchange.thalex.dto.trade.ThalexTradesDto;
import org.knowm.xchange.thalex.exceptions.ThalexException;
import si.mazi.rescu.ParamsDigest;

@Path("/api/v2/private/")
@Produces(MediaType.APPLICATION_JSON)
public interface ThalexAuthenticated {

  @GET
  @Path("account_summary")
  ThalexResponse<ThalexAccountSummaryDto> balances(
      @HeaderParam("Authorization") ParamsDigest signer)
      throws IOException, ThalexException;

  @GET
  @Path("trade_history")
  ThalexResponse<ThalexTradesDto> fills(
      @HeaderParam("Authorization") ParamsDigest signer,
      @QueryParam("limit") Integer limit,
      @QueryParam("time_low") Long from,
      @QueryParam("time_high") Long to,
      @QueryParam("bookmark") String bookmark)
      throws IOException, ThalexException;

  @GET
  @Path("transaction_history")
  ThalexResponse<ThalexTransactionsDto> transactions(
      @HeaderParam("Authorization") ParamsDigest signer,
      @QueryParam("limit") Integer limit,
      @QueryParam("time_low") Long from,
      @QueryParam("time_high") Long to,
      @QueryParam("bookmark") String bookmark)
      throws IOException, ThalexException;

  @GET
  @Path("portfolio")
  ThalexResponse<List<ThalexPortfolioDto>> portfolio(
      @HeaderParam("Authorization") ParamsDigest signer)
      throws IOException, ThalexException;

  @GET
  @Path("crypto_deposits")
  ThalexResponse<ThalexCryptoDeposits> cryptoDeposits(
      @HeaderParam("Authorization") ParamsDigest signer)
      throws IOException, ThalexException;

  @GET
  @Path("crypto_withdrawals")
  ThalexResponse<List<ThalexWithdrawal>> cryptoWithdrawals(
      @HeaderParam("Authorization") ParamsDigest signer)
      throws IOException, ThalexException;

  @GET
  @Path("daily_mark_history")
  ThalexResponse<ThalexDailyMarkHistoryResult> dailyMarkHistory(
      @HeaderParam("Authorization") ParamsDigest signer,
      @QueryParam("limit") Integer limit,
      @QueryParam("time_low") Long from,
      @QueryParam("time_high") Long to)
      throws IOException, ThalexException;
}
