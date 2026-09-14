package info.bitrich.xchangestream.gemini.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeminiOrderEventType {

  /**
   * Acknowledges your order events subscription and echoes back your parsed filters.
   */
  SUBSCRIPTION_ACK("subscription_ack"),

  /**
   * Sent at five-second intervals to show that your WebSocket connection to Gemini is alive. This
   * is filtered independently from the eventTypeFilter as heartbeat=true. The default for the
   * heartbeat parameter is true.
   */
  HEARTBEAT("heartbeat"),

  /**
   * At the time you begin your subscription, you receive a list of your current active orders. Each
   * active order will have the initial event type. You only see this event type at the beginning of
   * each subscription.
   */
  INITIAL("initial"),

  /**
   * Acknowledges that the exchange has received your order for initial processing. An order which
   * cannot be accepted for initial processing receives a rejected event.
   */
  ACCEPTED("accepted"),

  /**
   * When an order is rejected.
   */
  REJECTED("rejected"),

  /**
   * Your order is now visible on the Gemini order book. Under certain conditions, when you place an
   * order you will not receive a booked event. These include:
   * <p>
   * When your order is completely filled after being accepted When your order is accepted for
   * initial processing but then immediately cancelled because some condition cannot be fulfilled
   * (for instance, if you submit a maker-or-cancel order but your order would cross)
   */
  BOOKED("booked"),

  /**
   * When an order is filled.
   */
  FILL("fill"),

  /**
   * When an order is cancelled.
   */
  CANCELLED("cancelled"),

  /**
   * When your request to cancel an order cannot be fulfilled. Reasons this might happen include:
   * <p>
   * The order cannot be found
   */
  CANCEL_REJECTED("cancel_rejected"),

  /**
   * The last event in the order lifecycle: whether this order was completely filled or cancelled,
   * the consumer can use the closed event as a signal that the order is off the book on the Gemini
   * side.
   */
  CLOSED("closed");

  private final String geminiValue;

  @JsonCreator
  public static GeminiOrderEventType fromExchangeValue(String exchangeValue) {
    for (GeminiOrderEventType value : GeminiOrderEventType.values()) {
      if (value.geminiValue.equalsIgnoreCase(exchangeValue)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown Gemini order event type: " + exchangeValue);
  }
}
