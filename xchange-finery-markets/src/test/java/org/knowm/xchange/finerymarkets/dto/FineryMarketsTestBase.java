package org.knowm.xchange.finerymarkets.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;

public class FineryMarketsTestBase {

  ObjectMapper mapper;

  @Before
  public void setUp() {
    mapper = new ObjectMapper();
  }
}
