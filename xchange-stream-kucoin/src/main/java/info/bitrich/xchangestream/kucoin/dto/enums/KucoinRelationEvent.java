package info.bitrich.xchangestream.kucoin.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KucoinRelationEvent {

  // Main account
  MAIN_DEPOSIT("main.deposit"),
  MAIN_WITHDRAW_HOLD("main.withdraw_hold"),
  MAIN_WITHDRAW_DONE("main.withdraw_done"),
  MAIN_TRANSFER("main.transfer"),
  MAIN_OTHER("main.other"),

  // Trade account
  TRADE_HOLD("trade.hold"),
  TRADE_SETTED("trade.setted"),
  TRADE_TRANSFER("trade.transfer"),
  TRADE_OTHER("trade.other"),

  // trade_hf account
  TRADE_HF_HOLD("trade_hf.hold"),
  TRADE_HF_SETTED("trade_hf.setted"),
  TRADE_HF_TRANSFER("trade_hf.transfer"),
  TRADE_HF_OTHER("trade_hf.other"),

  // Margin account
  MARGIN_HOLD("margin.hold"),
  MARGIN_SETTED("margin.setted"),
  MARGIN_TRANSFER("margin.transfer"),
  MARGIN_OTHER("margin.other"),

  // Isolated margin account: isolated_{symbol}.xxx
  ISOLATED_HOLD("isolated_{symbol}.hold", true),
  ISOLATED_SETTED("isolated_{symbol}.setted", true),
  ISOLATED_TRANSFER("isolated_{symbol}.transfer", true),
  ISOLATED_OTHER("isolated_{symbol}.other", true),

  // margin_hf account
  MARGIN_V2_HOLD("marginV2.hold"),
  MARGIN_V2_SETTED("marginV2.setted"),
  MARGIN_V2_TRANSFER("marginV2.transfer"),
  MARGIN_V2_OTHER("marginV2.other"),

  // Isolated margin_hf account: isolatedV2_{symbol}.xxx
  ISOLATED_V2_HOLD("isolatedV2_{symbol}.hold", true),
  ISOLATED_V2_SETTED("isolatedV2_{symbol}.setted", true),
  ISOLATED_V2_TRANSFER("isolatedV2_{symbol}.transfer", true),
  ISOLATED_V2_OTHER("isolatedV2_{symbol}.other", true),

  // Fallback
  OTHER("other");

  @JsonValue
  private final String code;

  /** Whether this entry uses the {symbol} pattern instead of a fixed code. */
  private final boolean symbolPattern;

  KucoinRelationEvent(String code) {
    this(code, false);
  }

  @JsonCreator
  public static KucoinRelationEvent fromCode(String raw) {
    if (raw == null) {
      return OTHER;
    }

    // First, match fixed codes exactly
    for (KucoinRelationEvent value : values()) {
      if (!value.symbolPattern && value.code.equalsIgnoreCase(raw)) {
        return value;
      }
    }

    // Then, handle pattern-based codes (isolated_{symbol}.xxx, isolatedV2_{symbol}.xxx)
    for (KucoinRelationEvent value : values()) {
      if (value.symbolPattern && matchesPattern(value.code, raw)) {
        return value;
      }
    }

    // Unknown / new patterns => OTHER
    return OTHER;
  }

  /**
   * Check if a raw code matches a template with {symbol},
   * e.g. template "isolated_{symbol}.hold" vs raw "isolated_BTC-USDT.hold".
   */
  private static boolean matchesPattern(String template, String raw) {
    int start = template.indexOf("{symbol}");
    if (start < 0) {
      return false;
    }
    String prefix = template.substring(0, start); // e.g. "isolated_"
    String suffix = template.substring(start + "{symbol}".length()); // e.g. ".hold"
    return raw.startsWith(prefix) && raw.endsWith(suffix);
  }
}