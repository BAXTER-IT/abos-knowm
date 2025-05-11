package org.knowm.xchange.thalex.config.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.knowm.xchange.derivative.OptionsContract.OptionType;

public class ThalexStringToOptionTypeConverter extends StdConverter<String, OptionType> {

  @Override
  public OptionType convert(String value) {
    switch (value) {
      case "call":
        return OptionType.CALL;
      case "put":
        return OptionType.PUT;
      default:
        throw new IllegalArgumentException("Can't map " + value);
    }
  }
}
