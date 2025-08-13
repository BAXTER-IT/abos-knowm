package info.bitrich.xchangestream.thalex.dto;

import lombok.Data;

@Data
public class ThalexNotification<T> {

  private String channelName;
  private T notification;

}
