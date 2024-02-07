package org.knowm.xchange.bybit.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.ws.rs.core.Response.Status;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderStatus;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.enums.MarketParticipant;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;

public class BybitTradeServiceTest extends BaseWiremockTest {

  @Test
  public void testGetBybitOrder() throws IOException {
    Exchange bybitExchange = createExchange();
    BybitTradeService bybitAccountService = new BybitTradeService(bybitExchange);

    String orderDetails =
        "{\n"
            + "    \"retCode\": 0,\n"
            + "    \"retMsg\": \"OK\",\n"
            + "    \"result\": {\n"
            + "        \"list\": [\n"
            + "            {\n"
            + "                \"orderId\": \"fd4300ae-7847-404e-b947-b46980a4d140\",\n"
            + "                \"orderLinkId\": \"test-000005\",\n"
            + "                \"blockTradeId\": \"\",\n"
            + "                \"symbol\": \"ETHUSDT\",\n"
            + "                \"price\": \"1600.00\",\n"
            + "                \"qty\": \"0.10\",\n"
            + "                \"side\": \"Buy\",\n"
            + "                \"isLeverage\": \"\",\n"
            + "                \"positionIdx\": 1,\n"
            + "                \"orderStatus\": \"New\",\n"
            + "                \"cancelType\": \"UNKNOWN\",\n"
            + "                \"rejectReason\": \"EC_NoError\",\n"
            + "                \"avgPrice\": \"0\",\n"
            + "                \"leavesQty\": \"0.10\",\n"
            + "                \"leavesValue\": \"160\",\n"
            + "                \"cumExecQty\": \"0.00\",\n"
            + "                \"cumExecValue\": \"0\",\n"
            + "                \"cumExecFee\": \"0\",\n"
            + "                \"timeInForce\": \"GTC\",\n"
            + "                \"orderType\": \"Limit\",\n"
            + "                \"stopOrderType\": \"UNKNOWN\",\n"
            + "                \"orderIv\": \"\",\n"
            + "                \"triggerPrice\": \"0.00\",\n"
            + "                \"takeProfit\": \"2500.00\",\n"
            + "                \"stopLoss\": \"1500.00\",\n"
            + "                \"tpTriggerBy\": \"LastPrice\",\n"
            + "                \"slTriggerBy\": \"LastPrice\",\n"
            + "                \"triggerDirection\": 0,\n"
            + "                \"triggerBy\": \"UNKNOWN\",\n"
            + "                \"lastPriceOnCreated\": \"\",\n"
            + "                \"reduceOnly\": false,\n"
            + "                \"closeOnTrigger\": false,\n"
            + "                \"smpType\": \"None\",\n"
            + "                \"smpGroup\": 0,\n"
            + "                \"smpOrderId\": \"\",\n"
            + "                \"tpslMode\": \"Full\",\n"
            + "                \"tpLimitPrice\": \"\",\n"
            + "                \"slLimitPrice\": \"\",\n"
            + "                \"placeType\": \"\",\n"
            + "                \"createdTime\": \"1684738540559\",\n"
            + "                \"updatedTime\": \"1684738540561\"\n"
            + "            }\n"
            + "        ],\n"
            + "        \"nextPageCursor\": \"page_args%3Dfd4300ae-7847-404e-b947-b46980a4d140%26symbol%3D6%26\",\n"
            + "        \"category\": \"linear\"\n"
            + "    },\n"
            + "    \"retExtInfo\": {},\n"
            + "    \"time\": 1684765770483\n"
            + "}";

    stubFor(
        get(urlPathEqualTo("/v5/order/realtime"))
            .willReturn(
                aResponse()
                    .withStatus(Status.OK.getStatusCode())
                    .withHeader("Content-Type", "application/json")
                    .withBody(orderDetails)));

    Collection<Order> orders = bybitAccountService.getOrder("fd4300ae-7847-404e-b947-b46980a4d140");
    assertThat(orders.size()).isEqualTo(1);

    Order order = (Order) orders.toArray()[0];
    assertThat(order.getType()).isEqualTo(OrderType.BID);
    assertThat(order.getInstrument()).isEqualTo(new CurrencyPair("ETH", "USDT"));
    assertThat(order.getAveragePrice()).isEqualTo(new BigDecimal("0"));
    assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
    assertThat(order.getOriginalAmount()).isEqualTo(new BigDecimal("0.10"));
  }

  @Test
  public void testPlaceBybitOrder() throws IOException {
    Exchange bybitExchange = createExchange();
    BybitTradeService bybitAccountService = new BybitTradeService(bybitExchange);

    String orderPlacementResponse =
        "{\n"
            + "    \"retCode\": 0,\n"
            + "    \"retMsg\": \"OK\",\n"
            + "    \"result\": {\n"
            + "        \"orderId\": \"1321003749386327552\",\n"
            + "        \"orderLinkId\": \"spot-test-postonly\"\n"
            + "    },\n"
            + "    \"retExtInfo\": {},\n"
            + "    \"time\": 1672211918471\n"
            + "}";

    stubFor(
        post(urlPathEqualTo("/v5/order/create"))
            .willReturn(
                aResponse()
                    .withStatus(Status.OK.getStatusCode())
                    .withHeader("Content-Type", "application/json")
                    .withBody(orderPlacementResponse)));

    String orderId =
        bybitAccountService.placeMarketOrder(
            new MarketOrder(OrderType.ASK, new BigDecimal("0.1"), new CurrencyPair("BTC", "USDT")));

    assertThat(orderId).isEqualTo("1321003749386327552");
  }

  @Test
  public void testGetTradeHistory() throws IOException {
    Exchange bybitExchange = createExchange();
    BybitTradeService bybitTradeService = new BybitTradeService(bybitExchange);
    initGetStub("/v5/execution/list", "/getExecution.json");
    TradeHistoryParamsAll params = new TradeHistoryParamsAll();
    UserTrades tradeHistory = bybitTradeService.getTradeHistory(params);
    assertThat(tradeHistory.getUserTrades().size()).isEqualTo(3);

    // First trade by timestamp, so the last one in the response json
    UserTrade userTrade = tradeHistory.getUserTrades().get(0);

    assertEquals(OrderType.BID, userTrade.getType());
    assertEquals(new BigDecimal("0.000366"), userTrade.getOriginalAmount());
    assertEquals("BTC/USDT", userTrade.getInstrument().toString());
    assertEquals(new BigDecimal("40943"), userTrade.getPrice());
    assertEquals(new Date(1705931452698L), userTrade.getTimestamp());
    assertEquals("2290000000100204227", userTrade.getId());
    assertEquals("1603863704074720768", userTrade.getOrderId());
    assertEquals(new BigDecimal("0.000000366"), userTrade.getFeeAmount());
    assertEquals("1705931451897", userTrade.getOrderUserReference());
    assertEquals(MarketParticipant.TAKER, userTrade.getMarketParticipant());
  }
}
