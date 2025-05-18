package io.maddsoft.hbadgerstation.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.collection.NitriteId;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.filters.NitriteFilter;

@ToString
public class FilterCollection {

  @Getter
  private final HashMap<String, List<String>> filters = new HashMap<>();
  private final HashMap<String, List<NitriteId>> notIdFilters = new HashMap<>();
  private final HashMap<String, List<NitriteId>> idFilters = new HashMap<>();
  private String textFilter = StringUtils.EMPTY;

  public void setFilter(String filterName, List<String> filterValues) {
    if (filterValues == null || filterValues.isEmpty()) {
     filters.remove(filterName);
    } else {
      filters.put(filterName, filterValues);
    }
  }

  public void setTextFilter(String value) {
    if (StringUtils.isNotBlank(value) ) {
      textFilter = value;
    } else {
      textFilter = StringUtils.EMPTY;
    }
  }

  public void setIdFilter(String filterName, List<NitriteId> filterValues) {
    if (filterValues == null || filterValues.isEmpty()) {
      idFilters.remove(filterName);
    } else {
      idFilters.put(filterName, filterValues);
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
    return !filters.isEmpty() || !notIdFilters.isEmpty() || !idFilters.isEmpty() || StringUtils.isNotBlank(textFilter);
  }

  public void clearFilters() {
    filters.clear();
  }

  public NitriteFilter filtersToQuery(boolean withText) {
    NitriteFilter transformerdFilter = null;
    //always start with text filters
    if (withText && StringUtils.isNotBlank(textFilter)) {
      transformerdFilter = FluentFilter.where("searchIndex").text(textFilter);
    }
    transformerdFilter = buildStringInFilters(transformerdFilter);
    transformerdFilter = buildNotInIdFilters(transformerdFilter);
    transformerdFilter = buildInIdFilters(transformerdFilter);
    return transformerdFilter;
  }

  private NitriteFilter buildInIdFilters(NitriteFilter transformerdFilter) {
    for (Map.Entry<String, List<NitriteId>> entry: idFilters.entrySet()) {
      if(transformerdFilter == null) {
        transformerdFilter = FluentFilter.where(entry.getKey()).in(entry.getValue().toArray(new NitriteId[0]));
      } else {
        transformerdFilter = (NitriteFilter) transformerdFilter.and(FluentFilter.where(entry.getKey()).in(entry.getValue().toArray(new NitriteId[0])));
      }
    }
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
