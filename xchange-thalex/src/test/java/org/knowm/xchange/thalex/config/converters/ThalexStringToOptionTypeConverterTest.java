package org.knowm.xchange.thalex.config.converters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.knowm.xchange.derivative.OptionsContract.OptionType;

public class ThalexStringToOptionTypeConverterTest {

  @Test
  public void testConvert_call() {
    ThalexStringToOptionTypeConverter converter = new ThalexStringToOptionTypeConverter();
    assertEquals(OptionType.CALL, converter.convert("call"));
  }

  @Test
  public void testConvert_put() {
    ThalexStringToOptionTypeConverter converter = new ThalexStringToOptionTypeConverter();
    assertEquals(OptionType.PUT, converter.convert("put"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConvert_invalidValue() {
    ThalexStringToOptionTypeConverter converter = new ThalexStringToOptionTypeConverter();
    converter.convert("invalid");
  }
}