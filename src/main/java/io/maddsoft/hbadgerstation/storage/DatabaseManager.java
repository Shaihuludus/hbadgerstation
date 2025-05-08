package io.maddsoft.hbadgerstation.storage;

import io.maddsoft.hbadgerstation.storage.entities.Author;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
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

  public boolean addAuthor(Author author) {
    try {
      nitriteManager.getAuthorRepository().insert(author);
      return true;
    } catch (NitriteException e) {
      log.error(e.getMessage());
    }
      return false;
  }

  public WriteResult addPrintableThing(PrintableThing printableThing) {
    try {
      return nitriteManager.getPrintableThingRepository().insert(printableThing);
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

  public List<PrintableThing> getPrintableThings() {
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
    nitriteManager.deletePrintableThing(id);
  }
}
