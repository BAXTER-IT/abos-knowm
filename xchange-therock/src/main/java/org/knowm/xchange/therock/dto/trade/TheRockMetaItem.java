package org.knowm.xchange.therock.dto.trade;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/** @author Pnk */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TheRockMetaItem {

  private int page;
  private String href;

  public int getPage() {
    return page;
  }

  public String getHref() {
    return href;
  }
}
