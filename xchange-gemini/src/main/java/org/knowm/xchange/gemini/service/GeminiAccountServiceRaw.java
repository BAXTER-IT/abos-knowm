package org.knowm.xchange.gemini.service;

import static org.knowm.xchange.gemini.GeminiUtils.convertToGeminiCcyName;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.gemini.GeminiExchange;
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
import org.knowm.xchange.gemini.exceptions.GeminiException;
import org.knowm.xchange.instrument.Instrument;

public class GeminiAccountServiceRaw extends GeminiBaseService {

  protected final List<Instrument> allCurrencyPairs;

  public GeminiAccountServiceRaw(GeminiExchange exchange) {
    super(exchange);
    this.allCurrencyPairs =
            new ArrayList<>(exchange.getExchangeMetaData().getInstruments().keySet());
  }

  public List<GeminiTransferResponse> getGeminiTransfers(String currency, Long from, Integer limit) throws IOException {
    GeminiTransferRequest geminiTransferRequest = GeminiTransferRequest.builder().currency(currency).timestamp(
        from).limit(limit).build();
    return geminiAuthenticatedV1.transfers(apiKey, payloadCreator, signatureCreator,
        geminiTransferRequest);
  }

  public GeminiTransactionResponse getGeminiTransactions(Long from, Integer limit,
      String continuationToken) throws IOException {
    GeminiTransactionRequest geminiTransactionRequest = GeminiTransactionRequest.builder()
        .timestampNanos(from)
        .limit(limit)
        .continuationToken(continuationToken)
        .build();
    return geminiAuthenticatedV1.transactions(apiKey, payloadCreator, signatureCreator,
        geminiTransactionRequest
    );
  }

  public List<GeminiBalancesResponse> getGeminiBalances() throws IOException {
    try {
      return geminiAuthenticatedV1.balances(apiKey, payloadCreator, signatureCreator, GeminiBalancesRequest.builder()
          .build());
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public List<GeminiPositionsResponse> getGeminiPositions() throws IOException {
    try {
      return geminiAuthenticatedV1.positions(apiKey, payloadCreator, signatureCreator, GeminiPositionsRequest.builder()
          .build());
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public String withdraw(Currency currency, BigDecimal amount, String address) throws IOException {

    try {
      String ccy = convertToGeminiCcyName(currency.getCurrencyCode());
      GeminiWithdrawalRequest request =
          new GeminiWithdrawalRequest(
              String.valueOf(exchange.getNonceFactory().createValue()), ccy, amount, address);

      GeminiWithdrawalResponse withdrawRepsonse =
          geminiAuthenticatedV1.withdraw(apiKey, payloadCreator, signatureCreator, ccy, request);

      return withdrawRepsonse.txHash;
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public GeminiDepositAddressResponse requestDepositAddressRaw(Currency currency)
      throws IOException {
    try {
      String ccy = convertToGeminiCcyName(currency.getCurrencyCode());

      GeminiDepositAddressRequest exchange =
          new GeminiDepositAddressRequest(
              String.valueOf(this.exchange.getNonceFactory().createValue()), ccy, null);

      GeminiDepositAddressResponse requestDepositAddressResponse =
          geminiAuthenticatedV1.requestNewAddress(apiKey, payloadCreator, signatureCreator, ccy, exchange);
      if (requestDepositAddressResponse != null) {
        return requestDepositAddressResponse;
      } else {
        return null;
      }
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }

  public GeminiTrailingVolumeResponse Get30DayTrailingVolumeDescription() throws IOException {
    try {
      GeminiTrailingVolumeRequest request =
          new GeminiTrailingVolumeRequest(String.valueOf(exchange.getNonceFactory().createValue()));

      return geminiAuthenticatedV1.TrailingVolume(apiKey, payloadCreator, signatureCreator, request);
    } catch (GeminiException e) {
      throw handleException(e);
    }
  }
}
