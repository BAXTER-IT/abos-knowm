package info.bitrich.xchangestream.gateio.dto.request.payload;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class OrderedListPayload {

  @Singular
  @JsonValue
  private List<String> items;

}
