package info.bitrich.xchangestream.thalex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ThalexWsMessage {

  protected String method;
  @JsonInclude(Include.ALWAYS)
  protected Map<String, Object> params = new HashMap<>();
  private String id;
}
