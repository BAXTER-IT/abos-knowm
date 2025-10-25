package org.knowm.xchange.gemini.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.knowm.xchange.gemini.dto.account.GeminiTransactionTransferResponse;
import org.knowm.xchange.gemini.utils.DeserializationUtil;

public class GeminiTransactionTransferResponseDeserializer extends
    JsonDeserializer<GeminiTransactionTransferResponse> {

  @Override
  public GeminiTransactionTransferResponse deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    String rawJson = node.toString();

    return GeminiTransactionTransferResponse.builder()
        .timestamp(DeserializationUtil.getLong(node, "timestamp"))
        .source(DeserializationUtil.getText(node, "source"))
        .destination(DeserializationUtil.getText(node, "destination"))
        .operationReason(DeserializationUtil.getText(node, "operationReason"))
        .status(DeserializationUtil.getText(node, "status"))
        .eid(DeserializationUtil.getLong(node, "eid"))
        .currency(DeserializationUtil.getText(node, "currency"))
        .amount(DeserializationUtil.getBigDecimal(node, "amount"))
        .method(DeserializationUtil.getText(node, "method"))
        .correlationId(DeserializationUtil.getLong(node, "correlationId"))
        .transferType(DeserializationUtil.getText(node, "transferType"))
        .bankId(DeserializationUtil.getText(node, "bankId"))
        .purpose(DeserializationUtil.getText(node, "purpose"))
        .transactionHash(DeserializationUtil.getText(node, "transactionHash"))
        .transferId(DeserializationUtil.getText(node, "transferId"))
        .withdrawalId(DeserializationUtil.getText(node, "withdrawalId"))
        .clientTransferId(DeserializationUtil.getText(node, "clientTransferId"))
        .advanceEid(DeserializationUtil.getLong(node, "advanceEid"))
        .pendingEid(DeserializationUtil.getLong(node, "pendingEid"))
        .withdrawalEid(DeserializationUtil.getLong(node, "withdrawalEid"))
        .feeId(DeserializationUtil.getText(node, "feeId"))
        .type(DeserializationUtil.getText(node, "type"))
        .rawJson(rawJson)
        .build();
  }
}
