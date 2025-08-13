package org.knowm.xchange.thalex.dto.account;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class ThalexCryptoDeposits {

  /**
   * List of confirmed deposits.
   */
  private List<ThalexDeposit> confirmed;

  /**
   * List of unconfirmed deposits.
   */
  private List<ThalexDeposit> unconfirmed;
}
