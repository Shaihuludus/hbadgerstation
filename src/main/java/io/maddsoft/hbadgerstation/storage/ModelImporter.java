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

  private  List<String> printableFiles = new ArrayList<>();
  private List<String> images = new ArrayList<>();
  private List<String> otherFiles = new ArrayList<>();

  private boolean root = false;

  public ModelImporter(File directory, String authorName) {
    this.directory = directory;
    this.authorName = authorName;
    printableFilesExtensions = Settings.getSet("file types", "printableFiles");
    imagesExtensions = Settings.getSet("file types","images");
  }

  public ModelImporter(File directory, String authorName, boolean root) {
    this(directory, authorName);
    this.root = root;
  }

  public void importModels() {
    if (root) {
      File[] subFiles = directory.listFiles();
      if (subFiles != null && subFiles.length > 0) {
        Arrays.stream(subFiles).filter(File::isDirectory).forEach(this::importModel);
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

    databaseManager.addPrintableThing(builder.build());
  }

  private void prepareFiles(File subDirectory) {
    File[] files = subDirectory.listFiles();
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
