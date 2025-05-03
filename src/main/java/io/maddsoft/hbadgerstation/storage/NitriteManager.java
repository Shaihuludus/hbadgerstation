package io.maddsoft.hbadgerstation.storage;

import static org.dizitart.no2.common.util.Iterables.setOf;

import io.maddsoft.hbadgerstation.Settings;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing.PrintableConverter;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.common.mapper.SimpleNitriteMapper;
import org.dizitart.no2.repository.ObjectRepository;

@Slf4j
public class NitriteManager {

  private static NitriteManager instance = null;

  private Nitrite db;

  private NitriteManager() {
  }

  public static NitriteManager getInstance() {
    if (instance == null) {
      instance = new NitriteManager();
    }
    return instance;
  }

  private void openDb() {
    if (db != null) {
      db.close();
    }
log.info("Opening Nitrite DB...");
    MVStoreModule storeModule = MVStoreModule.withConfig()
        .filePath(Settings.getString("dbPath", "library.db"))
        .build();
    SimpleNitriteMapper documentMapper = new SimpleNitriteMapper();
    documentMapper.registerEntityConverter(new Author.AuthorConverter());
    documentMapper.registerEntityConverter(new PrintableConverter());

    db = Nitrite.builder()
        .loadModule(storeModule)
        .loadModule(() -> setOf(documentMapper))
        .openOrCreate();
  }

  public ObjectRepository<PrintableThing> getPrintableThingRepository() {
    if (db == null) {
      openDb();
    }
    return db.getRepository(PrintableThing.class);
  }

  public ObjectRepository<Author> getAuthorRepository() {
    if (db == null) {
      openDb();
    }
    return db.getRepository(Author.class);
  }

  public void closeDb() {
    if (db != null) {
      db.close();
    }
  }
}
