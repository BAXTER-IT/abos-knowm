package org.knowm.xchange.gateio.config.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.math.BigDecimal;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;

public class EntryToBalanceDeserializer extends JsonDeserializer<Balance> {

  @Override
  public Balance deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {

    ObjectCodec oc = p.getCodec();
    JsonNode node = oc.readTree(p);
    Currency currency = new Currency(node.path(0).asText());
    BigDecimal available = new BigDecimal(node.path(1).asText());
    return new Balance(currency, available);
  }
}
