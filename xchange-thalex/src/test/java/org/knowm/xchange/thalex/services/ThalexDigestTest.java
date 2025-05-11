package org.knowm.xchange.thalex.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.thalex.config.JwtGenerator;
import si.mazi.rescu.RestInvocation;
import si.mazi.rescu.RestMethodMetadata;

public class ThalexDigestTest {

  private JwtGenerator jwtGenerator;
  private ThalexDigest digest;

  @Before
  public void setUp() {
    jwtGenerator = mock(JwtGenerator.class);
    digest = new ThalexDigest("test-key", "test-secret", jwtGenerator);
  }

  @Test
  public void testDigestParams_success() throws Exception {
    when(jwtGenerator.generate("test-key", "test-secret")).thenReturn("mocked.jwt.token");

    RestInvocation invocation = mock(RestInvocation.class);
    RestMethodMetadata metadata = mock(RestMethodMetadata.class);
    when(invocation.getMethodMetadata()).thenReturn(metadata);
    when(metadata.getMethodName()).thenReturn("testMethod");

    String result = digest.digestParams(invocation);

    assertEquals("Bearer mocked.jwt.token", result);
  }

  @Test
  public void testDigestParams_exceptionThrown() throws Exception {
    when(jwtGenerator.generate("test-key", "test-secret")).thenThrow(new RuntimeException("JWT error"));

    RestInvocation invocation = mock(RestInvocation.class);
    RestMethodMetadata metadata = mock(RestMethodMetadata.class);
    when(invocation.getMethodMetadata()).thenReturn(metadata);
    when(metadata.getMethodName()).thenReturn("testMethod");

    try {
      digest.digestParams(invocation);
      fail("Expected ExchangeException to be thrown");
    } catch (ExchangeException ex) {
      assertTrue(ex.getMessage().contains("Error creating JWT for testMethod"));
      assertTrue(ex.getCause() instanceof RuntimeException);
    }
  }
}