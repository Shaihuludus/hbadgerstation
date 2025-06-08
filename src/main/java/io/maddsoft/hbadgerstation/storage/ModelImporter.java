package io.maddsoft.hbadgerstation.storage;

import io.maddsoft.hbadgerstation.Settings;
import io.maddsoft.hbadgerstation.cache.ImageCache;
import io.maddsoft.hbadgerstation.gui.GUIDefaults;
import io.maddsoft.hbadgerstation.storage.entities.Collection;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.dizitart.no2.collection.NitriteId;

public class ModelImporter {

  private final DatabaseManager databaseManager = new DatabaseManager();

  private final File directory;
  
  private final String authorName;

  private final Set<String> printableFilesExtensions;
  private final Set<String> imagesExtensions;

  private  List<String> printableFiles = new ArrayList<>();
  private List<String> images = new ArrayList<>();
  private List<String> otherFiles = new ArrayList<>();

  private boolean root = false;
  private boolean createCollection;
  private List<NitriteId> importedModels = new ArrayList<>();
  private List<String> collectionImages = new ArrayList<>();

  public ModelImporter(File directory, String authorName) {
    this.directory = directory;
    this.authorName = authorName;
    printableFilesExtensions = Settings.getSet("file types", "printableFiles", "stl,3mf");
    imagesExtensions = Settings.getSet("file types","images", "jpg,jpeg,png");
  }

  public ModelImporter(File directory, String authorName, boolean root, boolean createCollection) {
    this(directory, authorName);
    this.root = root;
    this.createCollection = createCollection;
    importedModels = new ArrayList<>();
    collectionImages = new ArrayList<>();
  }

  public void importModels() {
    if (root) {
      File[] subFiles = directory.listFiles();
      if (subFiles != null && subFiles.length > 0) {
        Arrays.stream(subFiles).forEach(subFile -> {
          if(subFile.isDirectory()) {
            this.importModel(subFile);
          }
          String extension = FilenameUtils.getExtension(subFile.getName()).toLowerCase();
          if (imagesExtensions.contains(extension)) {
            collectionImages.add(subFile.getAbsolutePath());
          }
        });
      }

      if (createCollection) {
        fillImageCache(collectionImages);
        Collection collection = Collection.builder()
            .printableThingIds(importedModels)
            .name(directory.getName())
            .images(collectionImages)
            .authorName(authorName)
            .build();
        databaseManager.addCollection(collection);
      }

    } else {
      importModel(directory);
    }
  }

  private void importModel(File modelDirectory) {
    printableFiles = new ArrayList<>();
    images = new ArrayList<>();
    otherFiles = new ArrayList<>();
    new AuthorImporter(authorName, "").importAuthor();
    prepareFiles(modelDirectory);
    PrintableThing.PrintableThingBuilder builder = PrintableThing.builder();
    builder.name(modelDirectory.getName());
    builder.authorName(authorName);
    builder.directoryPath(modelDirectory.getAbsolutePath());
    builder.directory(true);
    builder.printFilenames(printableFiles);
    builder.images(images);
    builder.otherFiles(otherFiles);

    NitriteId nitriteId = databaseManager.addPrintableThing(builder.build());
    if(createCollection) {
      importedModels.add(nitriteId);
    }
    fillImageCache(images);
  }

  private void fillImageCache(List<String> listToCache) {
    if (Settings.getInt(Settings.SECTION_CACHE, "cacheWhenImport", 1) == 1) {
      listToCache.forEach(image -> ImageCache.getImageCache().getCachedImage(image, GUIDefaults.IMAGE_DISPLAY_SIZE, GUIDefaults.IMAGE_DISPLAY_SIZE));
      if (!listToCache.isEmpty()) {
        ImageCache.getImageCache().getCachedImage(listToCache.getFirst(), GUIDefaults.PRINTABLE_IMAGE_MINIATURE_SIZE, GUIDefaults.PRINTABLE_IMAGE_MINIATURE_SIZE);
      }
    }
  }

  private void prepareFiles(File subDirectory) {
    File[] files = subDirectory.listFiles();
    if (files != null) {
      Arrays.stream(files).forEach(file -> {
        if (file.isDirectory()) {
          prepareFiles(file);
        }
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        if (printableFilesExtensions.contains(extension)) {
          printableFiles.add(file.getAbsolutePath());
        } else if (imagesExtensions.contains(extension)) {
          images.add(file.getAbsolutePath());
        } else  {
          otherFiles.add(file.getAbsolutePath());
        }
      });
    }
  }
}
