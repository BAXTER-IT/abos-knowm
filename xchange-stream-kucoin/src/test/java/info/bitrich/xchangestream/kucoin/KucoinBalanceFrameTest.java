package info.bitrich.xchangestream.kucoin;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsFuturesBalanceData;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsFuturesBalanceEvent;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsSpotBalanceData;
import info.bitrich.xchangestream.kucoin.dto.account.KucoinWsSpotBalanceEvent;
import info.bitrich.xchangestream.kucoin.dto.enums.KucoinRelationEvent;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;

/**
 * Unmarshalling and adapter tests for the two private balance channels,
 * {@code /account/balance} and {@code /contractAccount/wallet}.
 *
 * <p>The fixtures in {@code test/resources} are real exchange responses, captured
 * by holding both authenticated sockets open while funds were moved between a spot
 * account and its futures wallet. Only {@code walletBalance.change.with-open-position}
 * is constructed, and only because the captured frame came from an account with no
 * open position, where wallet balance and equity are necessarily equal and so cannot
 * demonstrate which of the two an adapter reads.
 */
class KucoinBalanceFrameTest {

  /**
   * The mapper the streaming services use, rather than a fresh one: it is configured
   * (unknown properties tolerated, unknown enum values defaulted, floating point read
   * as exact decimals), and a test on differently configured mapper would exercise
   * behaviour the production path never sees.
   */
  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  private <T> T read(String resource, Class<T> type) throws IOException {
    return mapper.readValue(getClass().getClassLoader().getResourceAsStream(resource), type);
  }

  private KucoinWsSpotBalanceData spot(String resource) throws IOException {
    return read(resource, KucoinWsSpotBalanceEvent.class).data;
  }

  private KucoinWsFuturesBalanceData futures(String resource) throws IOException {
    return read(resource, KucoinWsFuturesBalanceEvent.class).data;
  }

  @Test
  void unmarshalsSpotBalanceFrame() throws IOException {
    KucoinWsSpotBalanceData data = spot("account.balance.json");

    assertThat(data.getAccountId()).isEqualTo("180131510502404");
    assertThat(data.getCurrency()).isEqualTo(Currency.USDT);
    assertThat(data.getTotal()).isEqualByComparingTo("0");
    assertThat(data.getAvailable()).isEqualByComparingTo("0");
    assertThat(data.getHold()).isEqualByComparingTo("0");
    assertThat(data.getAvailableChange()).isEqualByComparingTo("-50");
    assertThat(data.getHoldChange()).isEqualByComparingTo("0");
    assertThat(data.getRelationEventId()).isEqualTo("6a8ef86ba9b2d300073d5397");
    assertThat(data.getTime()).isEqualTo(1787754603618L);
  }

  @Test
  void unmarshalsAmountsSentAsStringsIntoExactDecimals() throws IOException {
    // Every numeric value on this channel arrives as a JSON string, timestamps
    // included. Reading them as doubles would lose precision on large balances.
    KucoinWsSpotBalanceData data = spot("account.balance.json");

    assertThat(data.getAvailableChange())
        .isInstanceOf(BigDecimal.class)
        .isEqualByComparingTo(new BigDecimal("-50"));
  }

  @Test
  void unmarshalsRelationEventIdentifyingTheAccountThatMoved() throws IOException {
    // The channel reports every account type of the logged-in user, so the subject
    // alone does not say where the change happened. "trade.setted" is the trade
    // account; the same currency exists concurrently in main, margin and isolated.
    assertThat(spot("account.balance.json").getRelationEvent())
        .isEqualTo(KucoinRelationEvent.TRADE_SETTED);
  }

  @Test
  void adaptsSpotBalanceFrameToPostChangeBalance() throws IOException {
    Balance balance = KucoinStreamingAdapters.adaptBalance(spot("account.balance.json"));

    assertThat(balance.getCurrency()).isEqualTo(Currency.USDT);
    // The state after the change, not the -50 delta that produced it.
    assertThat(balance.getTotal()).isEqualByComparingTo("0");
    assertThat(balance.getAvailable()).isEqualByComparingTo("0");
    assertThat(balance.getFrozen()).isEqualByComparingTo("0");
    assertThat(balance.getTimestamp()).isEqualTo(new java.util.Date(1787754603618L));
  }

  @Test
  void adaptsAnEmptiedAccountToZeroRatherThanNull() throws IOException {
    // The exchange reports "0" for a drained account. A null would be
    // indistinguishable from an absent reading to a caller.
    Balance balance = KucoinStreamingAdapters.adaptBalance(spot("account.balance.json"));

    assertThat(balance.getTotal()).isNotNull().isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  void adaptsSpotBalanceFrameWithoutTimestamp() throws IOException {
    // An absent timestamp must cost the timestamp only. Constructing a Date from a
    // null Long would fail the whole frame over one missing field.
    Balance balance =
        KucoinStreamingAdapters.adaptBalance(spot("account.balance.no-timestamp.json"));

    assertThat(balance.getTotal()).isEqualByComparingTo("0");
    assertThat(balance.getTimestamp()).isNull();
  }

  @Test
  void unmarshalsFuturesWalletFrame() throws IOException {
    KucoinWsFuturesBalanceData data = futures("walletBalance.change.json");

    assertThat(data.getCurrency()).isEqualTo(Currency.USDT);
    assertThat(data.getWalletBalance()).isEqualByComparingTo("50");
    assertThat(data.getAvailableBalance()).isEqualByComparingTo("50");
    assertThat(data.getHoldBalance()).isEqualByComparingTo("0");
    assertThat(data.getEquity()).isEqualByComparingTo("50");
    assertThat(data.getMaxWithdrawAmount()).isEqualByComparingTo("50");
    assertThat(data.getTimestamp()).isEqualTo(1787754633355L);
    // Incremented by the exchange on every change, so a caller can detect a frame
    // it never received. The spot channel carries no equivalent.
    assertThat(data.getVersion()).isEqualTo("18");
  }

  @Test
  void unmarshalsFuturesWalletFrameIgnoringUndeclaredFields() throws IOException {
    // The captured frame carries availableMargin, which the DTO does not declare.
    // An exchange may add fields without notice, so an undeclared one must not fail
    // the frame.
    assertThat(futures("walletBalance.change.json").getCurrency()).isEqualTo(Currency.USDT);
  }

  @Test
  void adaptsFuturesWalletFrameToBalance() throws IOException {
    Balance balance = KucoinStreamingAdapters.adaptBalance(futures("walletBalance.change.json"));

    assertThat(balance.getCurrency()).isEqualTo(Currency.USDT);
    assertThat(balance.getTotal()).isEqualByComparingTo("50");
    assertThat(balance.getAvailable()).isEqualByComparingTo("50");
    assertThat(balance.getFrozen()).isEqualByComparingTo("0");
  }

  @Test
  void adaptsFuturesWalletBalanceRatherThanEquity() throws IOException {
    // Equity includes unrealised profit on open positions, so it moves with the
    // market while no funds are transferred. The wallet balance is the deposited
    // amount, which is what a Balance total means.
    KucoinWsFuturesBalanceData data = futures("walletBalance.change.with-open-position.json");
    Balance balance = KucoinStreamingAdapters.adaptBalance(data);

    assertThat(data.getEquity()).isEqualByComparingTo("57.25");
    assertThat(balance.getTotal()).isEqualByComparingTo("50");
    assertThat(balance.getAvailable()).isEqualByComparingTo("42.75");
    assertThat(balance.getFrozen()).isEqualByComparingTo("7.25");
  }

  @Test
  void adaptsNullDataToNull() {
    assertThat(KucoinStreamingAdapters.adaptBalance((KucoinWsSpotBalanceData) null)).isNull();
    assertThat(KucoinStreamingAdapters.adaptBalance((KucoinWsFuturesBalanceData) null)).isNull();
  }
}
