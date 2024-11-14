package org.knowm.xchange.dto.account;

import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.knowm.xchange.currency.Currency.BTC;
import static org.knowm.xchange.dto.account.FundingRecord.Type.DEPOSIT;

import org.junit.Test;
import org.knowm.xchange.dto.account.FundingRecord.Status;

public class FundingRecordStatusTest {

  @Test
  public void shouldProcessStatusDescriptionNormal() throws Exception {
    testStatusDesc("PROCESSING", "foo", Status.PROCESSING, "foo");
  }

  @Test
  public void shouldProcessStatusToUpercase() throws Exception {
    testStatusDesc("Complete", "bar", Status.COMPLETE, "bar");
  }

  @Test
  public void shouldProcessNullDescription() throws Exception {
    testStatusDesc("COMPLETE", null, Status.COMPLETE, null);
  }

  @Test
  public void shouldProcessStatusAsDescriptionWhenDescInputNull() throws Exception {
    testStatusDesc("Unknown", null, Status.FAILED, null);
  }

  private void testStatusDesc(
      String statusInput,
      String descriptionInput,
      FundingRecord.Status expectedStatus,
      String expectedDescription) {
    final FundingRecord fundingRecord = new FundingRecord.Builder()
        .setCurrency(BTC)
        .setAmount(ONE)
        .setType(DEPOSIT)
        .setStatus(Status.resolveStatus(statusInput))
        .setFee(ONE)
        .setBalance(ONE)
        .setDescription(descriptionInput)
        .build();

    assertThat(fundingRecord.getStatus()).isEqualTo(expectedStatus);
    assertThat(fundingRecord.getDescription()).isEqualTo(expectedDescription);
  }
}
