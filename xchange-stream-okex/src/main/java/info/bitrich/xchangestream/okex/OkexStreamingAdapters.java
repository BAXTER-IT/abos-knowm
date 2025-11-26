package info.bitrich.xchangestream.okex;

import info.bitrich.xchangestream.okex.dto.accont.OkexWsLedgerDto.OkexWsLedgerBalanceDto;
import java.util.Date;
import lombok.experimental.UtilityClass;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Status;

@UtilityClass
public class OkexStreamingAdapters {

  public static FundingRecord adaptFundingRecord(OkexWsLedgerBalanceDto in) {
    if (in == null) {
      return null;
    }

    return FundingRecord.builder()
        .currency(in.getCurrency())
        .balance(in.getCashBalance())
        .status(Status.COMPLETE)
        .date(new Date(in.getUpdateTime()))
        .build();
  }
}
