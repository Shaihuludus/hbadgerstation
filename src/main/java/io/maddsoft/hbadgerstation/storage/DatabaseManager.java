package io.maddsoft.hbadgerstation.storage;

import io.maddsoft.hbadgerstation.storage.entities.Author;
import io.maddsoft.hbadgerstation.storage.entities.Collection;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import io.maddsoft.hbadgerstation.storage.entities.TagEntity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.collection.NitriteId;
import org.dizitart.no2.common.WriteResult;
import org.dizitart.no2.exceptions.NitriteException;

@Slf4j
public class DatabaseManager {

  private final NitriteManager nitriteManager;

  public DatabaseManager() {
    nitriteManager = NitriteManager.getInstance();
  }

  public void addAuthor(Author author) {
    try {
      nitriteManager.getAuthorRepository().insert(author);
    } catch (NitriteException e) {
      log.error(e.getMessage());
    }
  }

  public NitriteId addPrintableThing(PrintableThing printableThing) {
    try {
      WriteResult insert = nitriteManager.getPrintableThingRepository().insert(printableThing);
      return insert.iterator().next();
    } catch (NitriteException e) {
      log.error(e.getMessage());
    }
    return null;
  }

  public boolean addAuthor(String authorName, String websiteUrl) {
    try {
      nitriteManager.getAuthorRepository().insert(Author.builder()
          .authorName(authorName)
          .websiteUrl(websiteUrl)
          .build());
      return true;
    } catch (NitriteException e) {
      log.error(e.getMessage());
    }
    return false;
  }

  public void close() {
    nitriteManager.closeDb();
  }

  public List<PrintableThing> getPrintableThings(FilterCollection filterCollection) {
    if (filterCollection.isFiltering()) {
      return nitriteManager.getPrintableThingRepository().find(filterCollection.filtersToQuery(true)).toList();
    }
    return nitriteManager.getPrintableThingRepository().find().toList();
  }

  public List<Author> getAuthors() {
    return nitriteManager.getAuthorRepository().find().toList();
  }

  public Author getAuthorByName(String authorName) {
    return nitriteManager.getAuthorRepository().getById(authorName);
  }

  public PrintableThing getPrintableThingById(NitriteId nitriteId) {
    return nitriteManager.getPrintableThingRepository().getById(nitriteId);
  }

  public void deletePrintableThing(NitriteId id) {
    nitriteManager. getPrintableThingRepository().remove(PrintableThing.builder().printableThingId(id).build());
  }

  public void updatePrintableThing(PrintableThing printableThing) {
    nitriteManager.getPrintableThingRepository().update(printableThing);
  }

  public Collection getCollectionById(NitriteId nitriteId) {
    return nitriteManager.getCollectionRepository().getById(nitriteId);
  }

  public List<Collection> getCollections(FilterCollection filterCollection) {
    if (filterCollection.isFiltering()) {
      return nitriteManager.getCollectionRepository().find(filterCollection.filtersToQuery(false)).toList();
    }
    return nitriteManager.getCollectionRepository().find().toList();
  }

  public void updateCollection(Collection collection) {
    nitriteManager.getCollectionRepository().update(collection);
  }

  public void addCollection(Collection collection) {
    try {
      nitriteManager.getCollectionRepository().insert(collection);
    } catch (NitriteException e) {
      log.error(e.getMessage());
    }
  }

  public List<String> getTags() {
    return nitriteManager.getTagsRepository()
        .find()
        .toList()
        .stream()
        .map(TagEntity::toString).toList();
  }

  public void addTag(String tagName) {
    if (null == nitriteManager.getTagsRepository().getById(tagName)) {
      nitriteManager.getTagsRepository().insert(new TagEntity(tagName));
    }
  }

  public void refreshSearchIndex() {
    List<PrintableThing> list = nitriteManager.getPrintableThingRepository().find().toList();
    list.forEach(printableThing -> nitriteManager.getPrintableThingRepository().update(printableThing));
  }
}
