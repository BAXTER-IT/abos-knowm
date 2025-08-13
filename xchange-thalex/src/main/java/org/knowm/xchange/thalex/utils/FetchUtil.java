package org.knowm.xchange.thalex.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class FetchUtil {

  public static <T, B> List<T> fetchAllPaginated(
      ThrowingFunction<String, B> fetchFunction,
      Function<B, List<T>> itemsExtractor,
      Function<B, String> bookmarkExtractor
  ) throws IOException {
    List<T> allItems = new ArrayList<>();
    String bookmark = null;

    do {
      log.debug(
          "Fetching batch with bookmark: {}", bookmark);
      B batch = fetchFunction.apply(bookmark);

      List<T> items = itemsExtractor.apply(batch);
      allItems.addAll(items);

      bookmark = bookmarkExtractor.apply(batch);
    } while (bookmark != null);

    log.debug("Completed pagination. Total items fetched: {}", allItems.size());

    return allItems;
  }

}
