package io.maddsoft.hbadgerstation.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dizitart.no2.collection.NitriteId;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.filters.NitriteFilter;

public class FilterCollection {

  private final HashMap<String, List<String>> filters = new HashMap<>();
  private final HashMap<String, List<NitriteId>> notIdFilters = new HashMap<>();

  public void setFilter(String filterName, List<String> filterValues) {
    if (filterValues == null || filterValues.isEmpty()) {
     filters.remove(filterName);
    } else {
      filters.put(filterName, filterValues);
    }
  }

  public void setNotIdFilter(String filterName, List<NitriteId> filterValues) {
    if (filterValues == null || filterValues.isEmpty()) {
      notIdFilters.remove(filterName);
    } else {
      notIdFilters.put(filterName, filterValues);
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
    transformerdFilter = buildStringInFilters(transformerdFilter);
    transformerdFilter = buildNotInIdFilters(transformerdFilter);
    return transformerdFilter;
  }

  private NitriteFilter buildNotInIdFilters(NitriteFilter transformerdFilter) {
    for (Map.Entry<String, List<NitriteId>> entry: notIdFilters.entrySet()) {
      if(transformerdFilter == null) {
        transformerdFilter = FluentFilter.where(entry.getKey()).notIn(entry.getValue().toArray(new NitriteId[0]));
      } else {
        transformerdFilter = (NitriteFilter) transformerdFilter.and(FluentFilter.where(entry.getKey()).notIn(entry.getValue().toArray(new NitriteId[0])));
      }
    }
    return transformerdFilter;
  }

  private NitriteFilter buildStringInFilters(NitriteFilter transformerdFilter) {
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
