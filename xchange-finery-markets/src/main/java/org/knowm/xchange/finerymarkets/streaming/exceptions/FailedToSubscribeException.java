package org.knowm.xchange.finerymarkets.streaming.exceptions;

import org.knowm.xchange.finerymarkets.streaming.enums.Feed;

public class FailedToSubscribeException extends RuntimeException {

  public FailedToSubscribeException(Feed feed) {
    super("Failed to subscribe to the feed: " + feed);
  }
}
