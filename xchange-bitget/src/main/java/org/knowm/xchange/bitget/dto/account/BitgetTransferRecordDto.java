package org.knowm.xchange.bitget.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.bitget.config.converter.StringToCurrencyConverter;
import org.knowm.xchange.bitget.config.converter.StringToFundingRecordStatusConverter;
import org.knowm.xchange.bitget.config.deserializer.BitgetTransferRecordDtoDeserializer;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.FundingRecord.Status;

@Data
@Builder
@JsonDeserialize(using = BitgetTransferRecordDtoDeserializer.class)
public class BitgetTransferRecordDto {

  @JsonProperty("clientOid")
  private String clientOid;

  @JsonProperty("transferId")
  private String transferId;

  @JsonProperty("coin")
  @JsonDeserialize(converter = StringToCurrencyConverter.class)
  private Currency currency;

  @JsonProperty("status")
  @JsonDeserialize(converter = StringToFundingRecordStatusConverter.class)
  private Status status;

  @JsonProperty("toType")
  private BitgetAccountType toAccountType;

  @JsonProperty("toSymbol")
  private String toSymbol;

  @JsonProperty("fromType")
  private BitgetAccountType fromAccountType;

  @JsonProperty("fromSymbol")
  private String fromSymbol;

  @JsonProperty("size")
  private BigDecimal size;

  @JsonProperty("ts")
  private Instant timestamp;

  private String rawJson;
}
