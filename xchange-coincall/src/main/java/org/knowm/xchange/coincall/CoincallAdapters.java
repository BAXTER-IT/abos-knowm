package org.knowm.xchange.coincall;

import static org.knowm.xchange.coincall.dtos.enums.CoincallUserType.SUBACCOUNT;
import static org.knowm.xchange.dto.account.OpenPosition.Type.LONG;
import static org.knowm.xchange.dto.account.OpenPosition.Type.SHORT;
import static org.knowm.xchange.dto.account.Wallet.WalletFeature.FUTURES_TRADING;
import static org.knowm.xchange.dto.account.Wallet.WalletFeature.MARGIN_TRADING;
import static org.knowm.xchange.dto.account.Wallet.WalletFeature.OPTIONS_TRADING;
import static org.knowm.xchange.dto.account.Wallet.WalletFeature.TRADING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.knowm.xchange.coincall.dtos.account.CoincallAccountDto;
import org.knowm.xchange.coincall.dtos.account.CoincallFuturesPositionDto;
import org.knowm.xchange.coincall.dtos.account.CoincallSubaccountTransferRecordDto;
import org.knowm.xchange.coincall.dtos.account.CoincallSystemTransferDto;
import org.knowm.xchange.coincall.dtos.account.CoincallTransactionDto;
import org.knowm.xchange.coincall.dtos.enums.CoincallSystemTransferSide;
import org.knowm.xchange.coincall.dtos.enums.CoincallSystemTransferState;
import org.knowm.xchange.coincall.dtos.enums.CoincallSystemTransferType;
import org.knowm.xchange.coincall.dtos.enums.CoincallTradeSide;
import org.knowm.xchange.coincall.dtos.enums.CoincallTransactionSide;
import org.knowm.xchange.coincall.dtos.enums.CoincallTransactionStatus;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallFuturesInstrumentDto;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallLotSizeFilter;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallPriceFilter;
import org.knowm.xchange.coincall.dtos.marketdata.CoincallSpotInstrumentDto;
import org.knowm.xchange.coincall.dtos.trade.CoincallFuturesTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallOptionTransactionDetail;
import org.knowm.xchange.coincall.dtos.trade.CoincallSpotFillDto;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.derivative.FuturesContract;
import org.knowm.xchange.derivative.OptionsContract;
import org.knowm.xchange.derivative.OptionsContract.OptionType;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Builder;
import org.knowm.xchange.dto.account.FundingRecord.Status;
import org.knowm.xchange.dto.account.OpenPosition;
import org.knowm.xchange.dto.account.OpenPosition.Type;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.enums.MarketParticipant;
import org.knowm.xchange.instrument.Instrument;

@UtilityClass
public class CoincallAdapters {

  public static Map<Instrument, InstrumentMetaData> toInstrumentsMap(
      List<CoincallSpotInstrumentDto> in) {
    return in.stream()
        .filter(CoincallSpotInstrumentDto::isEnableTrading)
        .map(CoincallAdapters::toInstrumentsMapEntry)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public static Map<Instrument, InstrumentMetaData> toInstumentsMap(
      List<CoincallFuturesInstrumentDto> in) {
    return in.stream()
        .map(CoincallAdapters::toInstrumentsMapEntry)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  static SimpleEntry<Instrument, InstrumentMetaData> toInstrumentsMapEntry(
      CoincallSpotInstrumentDto in) {
    CurrencyPair currencyPair = new CurrencyPair(in.getBaseCoin(), in.getQuoteCoin());
    InstrumentMetaData instrumentMetaData = buildSpotMeta(in);

    return new SimpleEntry<>(currencyPair, instrumentMetaData);
  }

  static InstrumentMetaData buildSpotMeta(CoincallSpotInstrumentDto dto) {
    InstrumentMetaData.Builder b = new InstrumentMetaData.Builder();

    CoincallLotSizeFilter lot = dto.getLotSizeFilter();
    CoincallPriceFilter price = dto.getPriceFilter();

    if (lot != null) {
      // Quantity limits
      if (lot.getMinQuantity() != null) {
        b.minimumAmount(lot.getMinQuantity());
      }
      if (lot.getMaxQuantity() != null) {
        b.maximumAmount(lot.getMaxQuantity());
      }

      // Counter limit (often notional size)
      if (lot.getMaxOrderSize() != null) {
        b.counterMaximumAmount(lot.getMaxOrderSize());
      }

      // Precision / scales
      b.volumeScale(lot.getBasePrecision());
      b.priceScale(lot.getQuotePrecision());

      // amount step size = 10^-basePrecision
      BigDecimal amountStep = BigDecimal.ONE.movePointLeft(lot.getBasePrecision());
      b.amountStepSize(amountStep);
    }

    if (price != null && price.getTickSize() != null) {
      b.priceStepSize(price.getTickSize());
    }

    // Trading fee currency = quote
    try {
      b.tradingFeeCurrency(Currency.getInstance(dto.getQuoteCoin()));
    } catch (Exception ignored) {
    }

    // Market order support → use enableTrading flag
    b.marketOrderEnabled(dto.isEnableTrading());

    return b.build();
  }

  static SimpleEntry<Instrument, InstrumentMetaData> toInstrumentsMapEntry(
      CoincallFuturesInstrumentDto in) {
    CurrencyPair currencyPair = new CurrencyPair(in.getBaseCurrency(), in.getQuoteCurrency());
    FuturesContract futuresContract = new FuturesContract(currencyPair,
        in.getProductType().toUpperCase());
    InstrumentMetaData instrumentMetaData = buildFuturesMeta(in);

    return new SimpleEntry<>(futuresContract, instrumentMetaData);
  }

  static InstrumentMetaData buildFuturesMeta(CoincallFuturesInstrumentDto dto) {
    InstrumentMetaData.Builder b = new InstrumentMetaData.Builder();

    // Infer price scale from last/index/contract price
    Integer priceScale = null;
    if (dto.getLastPrice() != null) {
      priceScale = dto.getLastPrice().scale();
    } else if (dto.getIndexPrice() != null) {
      priceScale = dto.getIndexPrice().scale();
    } else if (dto.getContractPrice() != null) {
      priceScale = dto.getContractPrice().scale();
    }

    if (priceScale != null) {
      b.priceScale(priceScale);
      b.priceStepSize(BigDecimal.ONE.movePointLeft(priceScale));
    }

    // Infer volume scale from baseVolume formatting
    if (dto.getBaseVolume() != null) {
      b.volumeScale(Math.max(0, dto.getBaseVolume().scale()));
    }

    // Trading fees in quote currency by convention
    try {
      b.tradingFeeCurrency(Currency.getInstance(dto.getQuoteCurrency()));
    } catch (Exception ignored) {
    }

    // Futures normally allow market orders
    b.marketOrderEnabled(true);

    return b.build();
  }

  public static UserTrade toUserTrade(CoincallSpotFillDto in) {
    OrderType type = in.getTradeSide() == CoincallTradeSide.BUY ? OrderType.BID : OrderType.ASK;

    // Build CurrencyPair from displaySymbol if available, fallback to raw symbol
    CurrencyPair currencyPair = toCurrencyPair(in.getDisplaySymbol());

    return new UserTrade.Builder()
        .type(type)
        .originalAmount(in.getQuantity())
        .instrument(currencyPair)
        .price(in.getPrice())
        .timestamp(new Date(in.getTimestamp()))
        .id(in.getTradeId())
        .orderId(String.valueOf(in.getOrderId()))
        .feeAmount(in.getFee())
        .feeCurrency(new Currency(in.getFeeCurrency()))
        .marketParticipant(in.isTaker() ? MarketParticipant.TAKER : MarketParticipant.MAKER)
        .orderUserReference(in.getClientOrderId())
        .rawJson(in.getRawJson())
        .build();
  }

  public static UserTrade toUserTrade(CoincallFuturesTransactionDetail in) {

    OrderType type =
        in.getTradeSide() == CoincallTradeSide.BUY ? OrderType.BID : OrderType.ASK;

    FuturesContract instrument = toFuturesContract(in.getDisplayName(), in.getSymbol());

    return new UserTrade.Builder()
        .type(type)
        .originalAmount(in.getQty())
        .instrument(instrument)
        .price(in.getPrice())
        .timestamp(new Date(in.getTime()))
        .id(String.valueOf(in.getTradeId()))
        .orderId(String.valueOf(in.getOrderId()))
        .feeAmount(in.getFee())
        .feeCurrency(Currency.USD) // always USD for futures
        .marketParticipant(in.isTaker() ? MarketParticipant.TAKER : MarketParticipant.MAKER)
        .rawJson(in.getRawJson())
        .build();
  }

  public static UserTrade toUserTrade(CoincallOptionTransactionDetail in) {

    OrderType type =
        in.getTradeSide() == CoincallTradeSide.BUY ? OrderType.BID : OrderType.ASK;

    OptionsContract instrument = toOptionsContract(in.getDisplayName());

    return new UserTrade.Builder()
        .type(type)
        .originalAmount(in.getQty())
        .instrument(instrument)
        .price(in.getPrice())
        .timestamp(new Date(in.getTime()))
        .id(String.valueOf(in.getTradeId()))
        .orderId(String.valueOf(in.getOrderId()))
        .feeAmount(in.getFee())
        .feeCurrency(Currency.USD)
        .marketParticipant(
            in.isTaker()
                ? MarketParticipant.TAKER
                : MarketParticipant.MAKER)
        .rawJson(in.getRawJson())
        .build();
  }

  public static Wallet toWallet(List<CoincallAccountDto> in) {
    List<Balance> balances =
        in.stream()
            .filter(balance -> balance.getAccountId() != null)
            .map(CoincallAdapters::toBalance)
            .collect(Collectors.toList());
    return Wallet.Builder.from(balances)
        .features(Set.of(TRADING, MARGIN_TRADING, FUTURES_TRADING, OPTIONS_TRADING)).build();
  }

  public static List<OpenPosition> toOpenPositions(List<CoincallFuturesPositionDto> in) {
    return in.stream().map(CoincallAdapters::toOpenPosition).collect(Collectors.toList());
  }

  public static OpenPosition toOpenPosition(CoincallFuturesPositionDto in) {
    FuturesContract futuresContract = toFuturesContract(in.getDisplayName(), in.getSymbol());
    BigDecimal size = in.getQty();
    Type type = size.compareTo(BigDecimal.ZERO) >= 0 ? LONG : SHORT;
    return new OpenPosition.Builder()
        .instrument(futuresContract)
        .price(in.getAvgPrice())
        .size(size)
        .type(type)
        .unRealisedPnl(in.getUpnl())
        .build();
  }

  public static List<FundingRecord> toFundingRecords(List<CoincallTransactionDto> in) {
    if (in == null) {
      return Collections.emptyList();
    }
    return in.stream()
        .filter(Objects::nonNull)
        .map(CoincallAdapters::toFundingRecord)
        .collect(Collectors.toList());
  }

  public static FundingRecord toFundingRecord(CoincallTransactionDto in) {
    if (in == null) {
      return null;
    }

    Currency currency = Currency.getInstance(in.getCoin());
    FundingRecord.Type type = toFundingType(in.getSide());
    FundingRecord.Status status = toFundingStatus(in.getStatus());

    String internalId = in.getTransactionRecordId() != null
        ? String.valueOf(in.getTransactionRecordId())
        : null;

    Date date = in.getCreateTime() != null
        ? Date.from(in.getCreateTime())
        : null;

    String description = buildDescription(in);

    return new FundingRecord.Builder()
        .setAddress(in.getAddress())
        .setDate(date)
        .setCurrency(currency)
        .setAmount(in.getAmount())
        .setInternalId(internalId)
        .setBlockchainTransactionHash(in.getTxId())
        .setType(type)
        .setStatus(status)
        .setFee(in.getServiceFee())
        .setDescription(description)
        .build();
  }

  public static List<FundingRecord> subaccountTransfersToFundingRecords(
      List<CoincallSubaccountTransferRecordDto> in) {
    if (in == null) {
      return Collections.emptyList();
    }
    return in.stream()
        .filter(Objects::nonNull)
        .map(CoincallAdapters::subaccountTransferToFundingRecord)
        .collect(Collectors.toList());
  }

  public static FundingRecord subaccountTransferToFundingRecord(
      CoincallSubaccountTransferRecordDto in) {
    if (in == null) {
      return null;
    }

    Currency currency = Currency.getInstance(in.getToken());
    Date date = in.getCreateTime() != null ? new Date(in.getCreateTime()) : null;

    String internalId = in.getId() != null ? String.valueOf(in.getId()) : null;

    String description = buildSubaccountTransferDescription(in);

    FundingRecord.Builder builder = new Builder()
        .setDate(date)
        .setCurrency(currency)
        .setAmount(in.getAmount())
        .setInternalId(internalId)
        .setType(FundingRecord.Type.INTERNAL_SUB_ACCOUNT_TRANSFER)
        .setStatus(Status.COMPLETE)
        .setDescription(description);

    if (in.getSourceUserType() == SUBACCOUNT && in.getSourceUserId() != null) {
      builder.setFromSubAccount(String.valueOf(in.getSourceUserId()));
    }

    if (in.getToUserType() == SUBACCOUNT && in.getToUserId() != null) {
      builder.setToSubAccount(String.valueOf(in.getToUserId()));
    }

    return builder.build();
  }

  static Balance toBalance(CoincallAccountDto in) {
    return new Balance.Builder()
        .currency(new Currency(in.getCoin()))
        .available(in.getAvailableBalance())
        .build();
  }

  static OptionsContract toOptionsContract(String displayName) {
    ParsedOptionSymbol p = parseOptionSymbol(displayName);

    return new OptionsContract.Builder()
        .currencyPair(new CurrencyPair(p.base, Currency.USD.getCurrencyCode()))
        .expireDate(p.expiry)
        .strike(p.strike)
        .type(p.type)
        .build();
  }

  static ParsedOptionSymbol parseOptionSymbol(String symbol) {
    if (symbol == null) {
      throw new IllegalArgumentException("Option symbol cannot be null");
    }

    String[] parts = symbol.split("-");
    if (parts.length != 4) {
      throw new IllegalArgumentException("Invalid option symbol: " + symbol);
    }

    ParsedOptionSymbol out = new ParsedOptionSymbol();

    out.base = parts[0].endsWith("USD") ? parts[0].substring(0, parts[0].length() - 3) : parts[0];

    out.expiry = parseExpiry(parts[1]);

    out.strike = new BigDecimal(parts[2]);

    out.type = parseOptionType(parts[3]);

    return out;
  }

  static Date parseExpiry(String raw) {
    DateTimeFormatter fmt = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("dMMMyy")
        .toFormatter(Locale.ENGLISH);

    LocalDate d = LocalDate.parse(raw.toUpperCase(), fmt);
    return Date.from(d.atStartOfDay(ZoneId.of("UTC")).toInstant());
  }

  static OptionType parseOptionType(String t) {
    if (t.equalsIgnoreCase("C")) {
      return OptionType.CALL;
    }
    if (t.equalsIgnoreCase("P")) {
      return OptionType.PUT;
    }
    throw new IllegalArgumentException("Invalid option type: " + t);
  }

  /**
   * @param in slash separated display symbol (e.g. XRP/USDT)
   * @return constructed currency pair
   */
  static CurrencyPair toCurrencyPair(String in) {
    String[] parts = in.split("/");
    return new CurrencyPair(parts[0], parts[1]);
  }

  static FuturesContract toFuturesContract(String displayName, String symbol) {
    String base = symbol.substring(0, symbol.length() - 3); // remove "USD"
    return new FuturesContract(new CurrencyPair(base, "USD"),
        displayName.substring(displayName.indexOf("-") + 1).toUpperCase());
  }

  static FundingRecord.Type toFundingType(CoincallTransactionSide side) {
    if (side == null) {
      return null;
    }
    switch (side) {
      case DEPOSIT:
        return FundingRecord.Type.DEPOSIT;
      case WITHDRAW:
        return FundingRecord.Type.WITHDRAWAL;
      default:
        return null;
    }
  }

  static FundingRecord.Status toFundingStatus(CoincallTransactionStatus status) {
    if (status == null) {
      return null;
    }
    switch (status) {
      case PROCESSING:
        return FundingRecord.Status.PROCESSING;
      case COMPLETED:
        return FundingRecord.Status.COMPLETE;
      case CANCEL:
      case CANCELLED:
        return FundingRecord.Status.CANCELLED;
      case FAILED:
        return FundingRecord.Status.FAILED;
      default:
        return null;
    }
  }

  static String buildDescription(CoincallTransactionDto in) {
    StringBuilder sb = new StringBuilder();

    appendPart(sb, "network", in.getNetwork());
    appendPart(sb, "status", in.getStatus() != null ? in.getStatus().getCode() : null);
    appendPart(sb, "type", in.getType() != null ? in.getType().getCode() : null);
    appendPart(sb, "addressUrl", in.getAddressUrl());
    appendPart(sb, "txIdUrl", in.getTxIdUrl());

    return sb.length() == 0 ? null : sb.toString();
  }

  static String buildSubaccountTransferDescription(CoincallSubaccountTransferRecordDto in) {
    StringBuilder sb = new StringBuilder();

    appendPart(sb, "fromUserId", String.valueOf(in.getSourceUserId()));
    appendPart(sb, "toUserId", String.valueOf(in.getToUserId()));
    appendPart(sb, "fromUserType",
        in.getSourceUserType() != null ? in.getSourceUserType().name() : null);
    appendPart(sb, "toUserType", in.getToUserType() != null ? in.getToUserType().name() : null);
    appendPart(sb, "fromUserName", in.getSourceUserName());
    appendPart(sb, "toUserName", in.getToUserName());
    appendPart(sb, "rawJson", in.getRawJson());

    return sb.length() == 0 ? null : sb.toString();
  }

  static void appendPart(StringBuilder sb, String key, String value) {
    if (value == null || value.isEmpty()) {
      return;
    }
    if (sb.length() > 0) {
      sb.append("; ");
    }
    sb.append(key).append('=').append(value);
  }

  public static FundingRecord toFundingRecord(UserTrade in) {
    if (in == null) {
      return null;
    }

    // Amount: trade size in "trade currency" (we just reuse originalAmount, always positive)
    BigDecimal amount = in.getOriginalAmount() == null ? null : in.getOriginalAmount().abs();

    BigDecimal fee = in.getFeeAmount() == null ? null : in.getFeeAmount().abs();

    return FundingRecord.builder()
        .date(in.getTimestamp())
        .currency(in.getInstrument().getBase())
        .amount(amount)
        .internalId(in.getId())
        .type(FundingRecord.Type.TRADE)
        .status(FundingRecord.Status.COMPLETE)
        .fee(fee)
        .description(in.getOrderUserReference())
        .rawJson(in.getRawJson())
        .build();
  }

  public static FundingRecord toFundingRecord(CoincallSystemTransferDto in) {
    if (in == null) {
      return null;
    }

    // Date from epoch millis
    Date date = in.getTime() != null ? new Date(in.getTime()) : null;

    // Currency from coin symbol (e.g. "BTC", "USDT")
    Currency currency = Currency.getInstance(in.getCoin());

    // Amount is already absolute in DTO
    BigDecimal amount = in.getCreditChange();

    FundingRecord.Type type = mapFundingType(in.getType(), in.getSide());
    FundingRecord.Status status = mapFundingStatus(in.getState());

    return FundingRecord.builder()
        .date(date)
        .currency(currency)
        .amount(amount)
        .internalId(in.getTxId())
        .type(type)
        .status(status)
        .description(in.getNote())
        .build();
  }

  static FundingRecord.Type mapFundingType(
      CoincallSystemTransferType type,
      CoincallSystemTransferSide side) {

    if (type == null) {
      // Fall back to generic inflow/outflow based on side (if we have it)
      return side == CoincallSystemTransferSide.INCREASE
          ? FundingRecord.Type.OTHER_INFLOW
          : FundingRecord.Type.OTHER_OUTFLOW;
    }

    switch (type) {
      case CREDIT:
      case TRIAL_BONUS:
        // Generic internal inflows/outflows
        return side == CoincallSystemTransferSide.DECREASE
            ? FundingRecord.Type.OTHER_OUTFLOW
            : FundingRecord.Type.OTHER_INFLOW;

      case REWARDS:
        // Treat rewards as airdrop-like inflows, outflows as generic
        return side == CoincallSystemTransferSide.DECREASE
            ? FundingRecord.Type.OTHER_OUTFLOW
            : FundingRecord.Type.AIRDROP;

      case TRANSFER:
        // Internal transfer between exchange accounts
        return side == CoincallSystemTransferSide.DECREASE
            ? FundingRecord.Type.INTERNAL_WITHDRAWAL
            : FundingRecord.Type.INTERNAL_DEPOSIT;

      case RELEASE_TRIAL_BONUS:
        // Reversing previously granted bonus – outflow from user perspective
        return FundingRecord.Type.OTHER_OUTFLOW;

      default:
        // Safe generic mapping
        return side == CoincallSystemTransferSide.DECREASE
            ? FundingRecord.Type.OTHER_OUTFLOW
            : FundingRecord.Type.OTHER_INFLOW;
    }
  }

  static FundingRecord.Status mapFundingStatus(CoincallSystemTransferState state) {
    if (state == null) {
      return null;
    }
    switch (state) {
      case UNSETTLED:
        return FundingRecord.Status.PROCESSING;
      case SETTLED:
        return FundingRecord.Status.COMPLETE;
      default:
        return null;
    }
  }

  static class ParsedOptionSymbol {

    String base;
    Date expiry;
    BigDecimal strike;
    OptionType type;
  }
}