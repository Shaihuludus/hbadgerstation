package io.maddsoft.hbadgerstation.storage;

import io.maddsoft.hbadgerstation.Settings;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;

public class ModelImporter {

  private final DatabaseManager databaseManager = new DatabaseManager();

  private final File directory;
  
  private final String authorName;

  private final Set<String> printableFilesExtensions;
  private final Set<String> imagesExtensions;

  private final List<String> printableFiles = new ArrayList<>();
  private final List<String> images = new ArrayList<>();
  private final List<String> otherFiles = new ArrayList<>();

  public ModelImporter(File directory, String authorName) {
    this.directory = directory;
    this.authorName = authorName;
    printableFilesExtensions = Settings.getSet("file types", "printableFiles");
    imagesExtensions = Settings.getSet("file types","images");
  }

  public void importModel() {
    new AuthorImporter(authorName, "").importAuthor();
    prepareFiles(directory);
    PrintableThing.PrintableThingBuilder builder = PrintableThing.builder();
    builder.name(directory.getName());
    builder.authorName(authorName);
    builder.directoryPath(directory.getAbsolutePath());
    builder.directory(true);
    builder.printFilenames(printableFiles);
    builder.images(images);
    builder.otherFiles(otherFiles);

    databaseManager.addPrintableThing(builder.build());
  }

  private void prepareFiles(File directory) {
    File[] files = directory.listFiles();
    if (files != null) {
      Arrays.stream(files).forEach(file -> {
        if (file.isDirectory()) {
          prepareFiles(file);
        }
        String extension = FilenameUtils.getExtension(file.getName());
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
