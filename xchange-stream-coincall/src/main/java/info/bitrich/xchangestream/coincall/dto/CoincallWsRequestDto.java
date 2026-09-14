package info.bitrich.xchangestream.coincall.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import info.bitrich.xchangestream.coincall.dto.enums.CoincallChannelAction;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class CoincallWsRequestDto<T> {

  private String sub;
  private String id;
  private CoincallChannelAction action;
  private String dataType;
  private T payload;

}
