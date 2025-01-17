package org.knowm.xchange.finerymarkets.dto;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DecoratedPayloadTest {

  DecoratedPayload payload;

  @Before
  public void setUp() throws Exception {
    payload = new DecoratedPayload() {
      @Override
      long getCurrentTime() {
        return 12345678;
      }
    };
  }

  @Test
  public void getPayloadString() {
    payload.setNonceAndTimestamp();
    assertEquals("{\"nonce\":12345678,\"timestamp\":12345678}", payload.getPayloadString());
  }
}
