package org.knowm.xchange.thalex;

import static org.knowm.xchange.dto.account.OpenPosition.Type.LONG;
import static org.knowm.xchange.dto.account.OpenPosition.Type.SHORT;
import static org.knowm.xchange.thalex.dto.enums.ThalexTransactionType.DEPOSIT;
import static org.knowm.xchange.thalex.dto.enums.ThalexTransactionType.WITHDRAWAL;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.derivative.CombinationsContract;
import org.knowm.xchange.derivative.CombinationsContract.Leg;
import org.knowm.xchange.derivative.FuturesContract;
import org.knowm.xchange.derivative.OptionsContract;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.FundingRecordBuilder;
import org.knowm.xchange.dto.account.FundingRecord.Status;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.account.OpenPosition.Type;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.enums.MarketParticipant;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.thalex.dto.ThalexCash;
import org.knowm.xchange.thalex.dto.account.ThalexAccountSummaryDto;
import org.knowm.xchange.thalex.dto.account.ThalexDeposit;
import org.knowm.xchange.thalex.dto.account.ThalexPortfolioDto;
import org.knowm.xchange.thalex.dto.account.ThalexTransactionDto;
import org.knowm.xchange.thalex.dto.account.ThalexWithdrawal;
import org.knowm.xchange.thalex.dto.enums.ThalexDirection;
import org.knowm.xchange.thalex.dto.enums.ThalexInstrumentType;
import org.knowm.xchange.thalex.dto.enums.ThalexTransactionType;
import org.knowm.xchange.thalex.dto.marketdata.ThalexInstrumentDto;
import org.knowm.xchange.thalex.dto.marketdata.ThalexInstrumentLegDto;
import org.knowm.xchange.thalex.dto.trade.ThalexTradeDto;

@UtilityClass
public class ThalexAdapters {

  public static final String DERIVATIVES = "derivatives";
  private static final String COUNTER_CURRENCY = "USD";
  private static final Map<String, Instrument> instrumentMap = new ConcurrentHashMap<>();

  public static Instrument getInstrumentFromMap(String instrumentName) {
    return instrumentMap.get(instrumentName);
  }

  public static void addInstrumentToMap(ThalexInstrumentDto instrumentDto) {
    instrumentMap.put(instrumentDto.getInstrumentName(), toInstrument(instrumentDto));
  }

  public static Wallet toWallet(ThalexAccountSummaryDto in) {
    List<Balance> balances =
        in.getCash().stream()
            .filter(ThalexCash::getTransactable)
            .map(ThalexAdapters::toBalance)
            .collect(Collectors.toList());
    return Wallet.Builder.from(balances).id(DERIVATIVES)
        .features(Collections.singleton(Wallet.WalletFeature.FUTURES_TRADING)).build();
  }

  static Balance toBalance(ThalexCash in) {
    return new Balance.Builder()
        .currency(in.getCurrency())
        .available(in.getBalance())
        .build();
  }

  static Date toDate(Instant instant) {
    return Optional.ofNullable(instant).map(Date::from).orElse(null);
  }

  public static UserTrade toUserTrade(ThalexTradeDto in) {
    return UserTrade.builder()
        .type(getOrderType(in))
        .originalAmount(in.getAmount())
        .instrument(getInstrumentFromMap(in.getInstrumentName()))
        .price(in.getPrice())
        .timestamp(toDate(in.getTime()))
        .id(in.getTradeId())
        .orderId(in.getOrderId())
        .feeAmount(in.getFee())
        .feeCurrency(new Currency(COUNTER_CURRENCY))
        .marketParticipant(getMarketParticipant(in))
        .rawJson(in.getRawJson())
        .build();
  }

  public static FundingRecord toFundingRecord(ThalexTransactionDto in) {
    FundingRecordBuilder fundingRecordBuilder = FundingRecord.builder()
        .currency(in.getAsset())
        .amount(in.getAmount())
        .date(toDate(in.getTime()))
        .description(in.getDescription())
        .balance(in.getBalanceAfter())
        .type(toFoundingRecordType(in.getTransactionType()))
        .rawJson(in.getRawJson());

    if (WITHDRAWAL.equals(in.getTransactionType())) {
      // example description: "withdrawal 0xdac17f958d2ee523a2206206994597c13d831ec7 <no_label>"
      String fromAddress = in.getDescription().split(" ")[1];
      fundingRecordBuilder.address(fromAddress);
    }

    if (DEPOSIT.equals(in.getTransactionType())) {
      // example description: "chain=testnet tx=xx_fake_8d5a2ef7683b7dc5d1c0d46a52ac3f40"
      String blockChainHash = in.getDescription().split(" ")[1].substring(3);
      fundingRecordBuilder.blockchainTransactionHash(blockChainHash);
    }

    return fundingRecordBuilder.build();
  }

  public static FundingRecord toFundingRecord(ThalexDeposit deposit) {
    return FundingRecord.builder()
        .date(toDate(deposit.getTransactionTimestamp()))
        .currency(deposit.getCurrency())
        .amount(deposit.getAmount())
        .blockchainTransactionHash(deposit.getTransactionHash())
        .type(FundingRecord.Type.DEPOSIT)
        .status(Status.resolveStatus(deposit.getStatus().name()))
        .build();
  }

  public static FundingRecord toFundingRecord(ThalexWithdrawal withdrawal) {
    return FundingRecord.builder()
        .address(withdrawal.getTargetAddress())
        .date(Date.from(withdrawal.getCreateTime()))
        .currency(withdrawal.getCurrency())
        .amount(withdrawal.getAmount())
        .blockchainTransactionHash(withdrawal.getTransactionHash())
        .type(FundingRecord.Type.WITHDRAWAL)
        .status(Status.resolveStatus(withdrawal.getState().name()))
        .fee(withdrawal.getFee())
        .description(withdrawal.getRemark())
        .build();
  }

  public static List<OpenPosition> toOpenPositions(List<ThalexPortfolioDto> portfolios) {
    return portfolios.stream()
        .map(ThalexAdapters::toOpenPosition)
        .collect(Collectors.toList());
  }

  static OrderType getOrderType(ThalexTradeDto in) {
    if (in.getDirection() == null) {
      throw new IllegalArgumentException("Can't map null direction");
    }
    OrderType orderType;
    switch (in.getDirection()) {
      case BUY:
        orderType = OrderType.BID;
        break;
      case SELL:
        orderType = OrderType.ASK;
        break;
      default:
        throw new IllegalArgumentException("Can't map " + in.getDirection());
    }
    return orderType;
  }

  static MarketParticipant getMarketParticipant(ThalexTradeDto in) {
    if (in.getMakerTaker() == null) {
      if (in.getDirection() == ThalexDirection.BUY) {
        throw new IllegalArgumentException("Can't map null makerTaker for buy direction");
      }
      return null;
    }

    MarketParticipant marketParticipant;
    switch (in.getMakerTaker()) {
      case TAKER:
        marketParticipant = MarketParticipant.TAKER;
        break;
      case MAKER:
        marketParticipant = MarketParticipant.MAKER;
        break;
      default:
        throw new IllegalArgumentException("Can't map " + in.getMakerTaker());
    }
    return marketParticipant;
  }

  static OpenPosition toOpenPosition(ThalexPortfolioDto portfolio) {
    BigDecimal size = portfolio.getPosition();
    Type type = size.compareTo(BigDecimal.ZERO) >= 0 ? LONG : SHORT;
    return new OpenPosition.Builder()
        .price(portfolio.getAveragePrice())
        .size(size)
        .type(type)
        .unRealisedPnl(portfolio.getUnrealisedPnl())
        .build();
  }

  public static Map<Instrument, InstrumentMetaData> toInstrumentsMap(List<ThalexInstrumentDto> in) {
    return in.stream().map(ThalexAdapters::toInstrumentsMapEntry).filter(Objects::nonNull)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  static SimpleEntry<Instrument, InstrumentMetaData> toInstrumentsMapEntry(
      ThalexInstrumentDto instrumentDto) {
    ThalexInstrumentType instrumentType = instrumentDto.getType();
    if (instrumentType == null) {
      return null;
    }
    switch (instrumentType) {
      case OPTION:
        return toOptionsEntry(instrumentDto);
      case FUTURE:
      case PERPETUAL:
        return toFuturesTypeEntry(instrumentDto);
      case COMBINATION:
        return toCombinationsEntry(instrumentDto);
      default:
        throw new IllegalArgumentException("Can't map " + instrumentType);
    }
  }

  static SimpleEntry<Instrument, InstrumentMetaData> toOptionsEntry(
      ThalexInstrumentDto in) {
    OptionsContract contract = toOptionsContract(in);
    InstrumentMetaData instrumentMetaData = getInstrumentMetaData(in);
    return new SimpleEntry<>(contract, instrumentMetaData);
  }

  static SimpleEntry<Instrument, InstrumentMetaData> toFuturesTypeEntry(ThalexInstrumentDto in) {
    FuturesContract contract = toFuturesTypeContract(in);
    InstrumentMetaData instrumentMetaData = getInstrumentMetaData(in);
    return new SimpleEntry<>(contract, instrumentMetaData);
  }

  static SimpleEntry<Instrument, InstrumentMetaData> toCombinationsEntry(
      ThalexInstrumentDto in) {
    CombinationsContract contract = toCombinationsContract(in);
    InstrumentMetaData instrumentMetaData = getInstrumentMetaData(in);
    return new SimpleEntry<>(contract, instrumentMetaData);
  }

  static InstrumentMetaData getInstrumentMetaData(ThalexInstrumentDto in) {
    return InstrumentMetaData.builder()
        .minimumAmount(in.getMinOrderAmount())
        .priceStepSize(in.getTickSize())
        .amountStepSize(in.getVolumeTickSize())
        .marketOrderEnabled(true)
        .build();
  }

  static Instrument toInstrument(ThalexInstrumentDto in) {
    ThalexInstrumentType instrumentType = in.getType();
    if (instrumentType == null) {
      return null;
    }
    switch (instrumentType) {
      case OPTION:
        return toOptionsContract(in);
      case FUTURE:
      case PERPETUAL:
        return toFuturesTypeContract(in);
      case COMBINATION:
        return toCombinationsContract(in);
      default:
        throw new IllegalArgumentException("Can't map " + instrumentType);
    }
  }

  static OptionsContract toOptionsContract(ThalexInstrumentDto in) {
    String baseCurrency = in.getInstrumentName().split("-")[0];
    CurrencyPair currencyPair = new CurrencyPair(baseCurrency, COUNTER_CURRENCY);
    return new OptionsContract.Builder()
        .currencyPair(currencyPair)
        .expireDate(toDate(in.getExpirationTimestamp()))
        .strike(in.getStrikePrice())
        .type(in.getOptionType())
        .build();
  }

  /**
   * @return Perpetual (props is PERPETUAL) or future FuturesContract
   */
  static FuturesContract toFuturesTypeContract(ThalexInstrumentDto in) {
    String[] instrumentNameParts = in.getInstrumentName().split("-");
    String baseCurrency = instrumentNameParts[0];
    CurrencyPair currencyPair = new CurrencyPair(baseCurrency, COUNTER_CURRENCY);
    if ("PERPETUAL".equalsIgnoreCase(instrumentNameParts[1])) {
      return new FuturesContract(currencyPair, "PERPETUAL");
    }
    return new FuturesContract(
        currencyPair,
        instrumentNameParts[1].toUpperCase());
  }

  static CombinationsContract toCombinationsContract(ThalexInstrumentDto in) {
    String baseCurrency = in.getInstrumentName().split("-")[0];
    CurrencyPair currencyPair = new CurrencyPair(baseCurrency, COUNTER_CURRENCY);
    List<Leg> legs = in.getLegs().stream().map(ThalexAdapters::toLeg).collect(Collectors.toList());
    return CombinationsContract.builder()
        .currencyPair(currencyPair)
        .instrumentName(in.getInstrumentName())
        .expireDate(toDate(in.getExpirationTimestamp()))
        .legs(legs)
        .build();
  }

  static Leg toLeg(ThalexInstrumentLegDto in) {
    return Leg.builder()
        .instrumentName(in.getInstrumentName())
        .quantity(in.getQuantity())
        .build();
  }

  public static FundingRecord.Type toFoundingRecordType(ThalexTransactionType in) {
    if (in == null) {
      return null;
    }

    switch (in) {
      case DEPOSIT:
        return FundingRecord.Type.DEPOSIT;
      case WITHDRAWAL:
        return FundingRecord.Type.WITHDRAWAL;
      case WITHDRAWAL_FEE:
        return FundingRecord.Type.FEE;
      case SESSION_SETTLEMENT:
        return FundingRecord.Type.SETTLEMENT;
      case PERPETUAL_FUNDING:
      case DAILY_INTEREST:
        return FundingRecord.Type.INTEREST;
      case INTERNAL_TRANSFER:
        return FundingRecord.Type.INTERNAL_WALLET_TRANSFER;
      case ASSET_SWAP:
        return FundingRecord.Type.TRADE;
      case REFERRAL_PROGRAM_PAYMENT:
      case MARKET_VELOCITY_PROGRAM_PAYMENT:
      case MARKET_QUALITY_PROGRAM_PAYMENT:
        return FundingRecord.Type.OTHER_INFLOW;
      default:
        throw new IllegalArgumentException("Unknown ThalexTransactionType: " + in);
    }
  }
}
