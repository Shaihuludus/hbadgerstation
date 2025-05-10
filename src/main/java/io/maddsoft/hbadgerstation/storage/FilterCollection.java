package io.maddsoft.hbadgerstation.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.filters.NitriteFilter;

public class FilterCollection {

  private final HashMap<String, List<String>> filters = new HashMap<>();

  public void setFilter(String filterName, List<String> filterValues) {
    if (filterValues == null || filterValues.isEmpty()) {
     filters.remove(filterName);
    } else {
      filters.put(filterName, filterValues);
    }
  }

  public boolean isFiltering() {
    return !filters.isEmpty();
  }

  public void clearFilters() {
    filters.clear();
  }

  public NitriteFilter filtersToQuery() {
    NitriteFilter transformerdFilter = null;
    for (Map.Entry<String, List<String>> entry: filters.entrySet()) {
      if(transformerdFilter == null) {
        transformerdFilter = FluentFilter.where(entry.getKey()).in(entry.getValue().toArray(new String[0]));
      } else {
          transformerdFilter = (NitriteFilter) transformerdFilter.and(FluentFilter.where(entry.getKey()).in(entry.getValue().toArray(new String[0])));
        }
      }
    return transformerdFilter;
  }
}
