package org.knowm.xchange.finerymarkets.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsCurrency;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsInstrument;
import org.knowm.xchange.finerymarkets.dto.marketdata.FineryMarketsNetwork;
import org.knowm.xchange.finerymarkets.dto.marketdata.response.InstrumentsResponse;

@Slf4j
public class FineryMarketsInstrumentsResponseDeserializer
    extends JsonDeserializer<InstrumentsResponse> {

  @Override
  public InstrumentsResponse deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    InstrumentsResponse response = new InstrumentsResponse();

    ObjectMapper mapper = new ObjectMapper();
    if (!node.isArray()) {
      log.error("FineMarketsInstrumentsResponseDeserializer: node is not an array");
      throw new IOException("FineMarketsInstrumentsResponseDeserializer: node is not an array");
    }

    JsonNode currencies = node.get(0);

    if (currencies != null && currencies.isArray()) {
      List<FineryMarketsCurrency> list = new ArrayList<>();
      for (JsonNode cryptoNode : node.get(0)) {
        FineryMarketsCurrency entry = mapper.treeToValue(cryptoNode, FineryMarketsCurrency.class);
        list.add(entry);
      }
      response.setCurrencies(list);
    }

    JsonNode instruments = node.get(1);

    if (instruments != null && instruments.isArray()) {
      List<FineryMarketsInstrument> list = new ArrayList<>();
      for (JsonNode cryptoNode : node.get(1)) {
        FineryMarketsInstrument entry =
            mapper.treeToValue(cryptoNode, FineryMarketsInstrument.class);
        list.add(entry);
      }
      response.setInstruments(list);
    }

    JsonNode networks = node.get(2);

    if (networks != null) {
      if (networks.isArray()) {
        List<FineryMarketsNetwork> list = new ArrayList<>();
        for (JsonNode cryptoNode : node.get(2)) {
          FineryMarketsNetwork entry = mapper.treeToValue(cryptoNode, FineryMarketsNetwork.class);
          list.add(entry);
        }
        response.setNetworks(list);
      }
    } else {
      response.setNetworks(Collections.emptyList());
    }

    return response;
  }
}
