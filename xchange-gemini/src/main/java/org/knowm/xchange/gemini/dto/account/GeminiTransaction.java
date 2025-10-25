package org.knowm.xchange.gemini.dto.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.knowm.xchange.gemini.deserializers.GeminiTransactionResponseDeserializer;

@JsonDeserialize(using = GeminiTransactionResponseDeserializer.class)
public interface GeminiTransaction {

}
