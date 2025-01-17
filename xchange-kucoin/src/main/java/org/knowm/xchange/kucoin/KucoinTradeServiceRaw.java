package org.knowm.xchange.kucoin;

import static org.knowm.xchange.kucoin.KucoinExceptionClassifier.classifyingExceptions;

import java.io.IOException;
import java.util.List;
import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.kucoin.dto.request.OrderCreateApiRequest;
import org.knowm.xchange.kucoin.dto.response.HistOrdersResponse;
import org.knowm.xchange.kucoin.dto.response.OrderCancelResponse;
import org.knowm.xchange.kucoin.dto.response.OrderCreateResponse;
import org.knowm.xchange.kucoin.dto.response.OrderResponse;
import org.knowm.xchange.kucoin.dto.response.Pagination;
import org.knowm.xchange.kucoin.dto.response.TradeResponse;

public class KucoinTradeServiceRaw extends KucoinBaseService {

  protected KucoinTradeServiceRaw(
      KucoinExchange exchange, ResilienceRegistries resilienceRegistries) {
    super(exchange, resilienceRegistries);
  }

  public Pagination<OrderResponse> getKucoinOpenOrders(String symbol, int page, int pageSize)
      throws IOException {
    return getKucoinOrders(symbol, "active", page, pageSize);
  }

  public Pagination<OrderResponse> getKucoinClosedOrders(String symbol, int page, int pageSize)
      throws IOException {
    return getKucoinOrders(symbol, "done", page, pageSize);
  }

  public Pagination<OrderResponse> getKucoinOrders(
      String symbol, String status, int page, int pageSize) throws IOException {
    checkAuthenticated();
    return classifyingExceptions(
        () ->
            orderApi.queryOrders(
                apiKey,
                digest,
                nonceFactory,
                passphrase,
                "2",
                symbol,
                null,
                null,
                status,
                null,
                null,
                pageSize,
                page));
  }

  public OrderResponse getKucoinOrder(String id) throws IOException {
    checkAuthenticated();
    return classifyingExceptions(
        () -> orderApi.getOrder(apiKey, digest, nonceFactory, passphrase, "2", id));
  }

  public Pagination<TradeResponse> getKucoinFills(
      String symbol, String orderId, int page, int pageSize, Long startAt, Long endAt)
      throws IOException {
    checkAuthenticated();
    return classifyingExceptions(
        () ->
            fillApi.queryTrades(
                apiKey,
                digest,
                nonceFactory,
                passphrase,
                "2",
                symbol,
                orderId,
                null,
                null,
                startAt,
                endAt,
                pageSize,
                page));
  }

  public Pagination<HistOrdersResponse> getKucoinHistOrders(
      String symbol, int page, int pageSize, Long startAt, Long endAt) throws IOException {
    checkAuthenticated();
    return classifyingExceptions(
        () ->
            histOrdersApi.queryHistOrders(
                apiKey,
                digest,
                nonceFactory,
                passphrase,
                "2",
                symbol,
                null,
                startAt,
                endAt,
                pageSize,
                page));
  }

  public OrderCancelResponse kucoinCancelAllOrders(String symbol) throws IOException {
    checkAuthenticated();
    return classifyingExceptions(
        () -> orderApi.cancelOrders(apiKey, digest, nonceFactory, passphrase, "2", symbol));
  }

  public OrderCancelResponse kucoinCancelOrder(String orderId) throws IOException {
    checkAuthenticated();
    return classifyingExceptions(
        () -> orderApi.cancelOrder(apiKey, digest, nonceFactory, passphrase, "2", orderId));
  }

  public OrderCreateResponse kucoinCreateOrder(OrderCreateApiRequest opsRequest)
      throws IOException {
    checkAuthenticated();
    return classifyingExceptions(
        () -> orderApi.createOrder(apiKey, digest, nonceFactory, passphrase, "2", opsRequest));
  }

  public List<OrderResponse> getKucoinRecentOrders() throws IOException {
    this.checkAuthenticated();
    return classifyingExceptions(
        () ->
            limitOrderAPI.getRecentOrders(
                this.apiKey, this.digest, this.nonceFactory, this.passphrase, "2"));
  }
}
