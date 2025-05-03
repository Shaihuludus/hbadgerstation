package io.maddsoft.hbadgerstation;

import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.dizitart.no2.common.WriteResult;

public class ImportModelAndAuthor {

  DatabaseManager databaseManager = new DatabaseManager();

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Please enter the name of the directory you wish to import");
      return;
    }
    new ImportModelAndAuthor().importData(args);
  }

  public void importData(String[] args) {
    String directory = args[0];
    File directoryFile = new File(directory);
    if (!directoryFile.exists()) {
      System.out.println("Directory does not exist");
      return;
    }

    PrintableThing.PrintableThingBuilder printableThingBuilder = PrintableThing.builder();

    Author.AuthorBuilder authorBuilder = Author.builder();

    if (args.length > 1) {
      authorBuilder.authorName(args[1]);
      if (args.length > 2) {
        authorBuilder.websiteUrl(args[2]);
      } else {
        authorBuilder.websiteUrl("");
      }
      Author author = authorBuilder.build();
      printableThingBuilder.authorName(author.getAuthorName());
      databaseManager.addAuthor(author);
    }

    printableThingBuilder.directory(true);
    printableThingBuilder.name(directoryFile.getName());
    printableThingBuilder.directoryPath(directory);
    List<String> printFiles = new ArrayList<>();
    List<String> images = new ArrayList<>();
    List<String> otherFiles = new ArrayList<>();
    checkDir(directoryFile, printFiles, images, otherFiles);
    printableThingBuilder.printFilenames(printFiles);
    printableThingBuilder.images(images);
    printableThingBuilder.otherFiles(otherFiles);
    PrintableThing builded = printableThingBuilder.build();
    WriteResult result =databaseManager.addPrintableThing(builded);
    System.out.println("Imported data: "+builded);
    System.out.println(result);
    databaseManager.close();

  }

  private void checkDir(File directoryFile, List<String> printFiles, List<String> images,
      List<String> otherFiles) {
    File[] files = directoryFile.listFiles();
    if (files != null) {
      Arrays.stream(files)
          .forEach(file -> {
            if (file.isDirectory()) {
              checkDir(file, printFiles, images, otherFiles);
            } else {
              String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
              if (Settings.getSet("printableFiles").contains(extension)) {
                printFiles.add(file.getAbsolutePath());
              } else if (Settings.getSet("images").contains(extension)) {
                images.add(file.getAbsolutePath());
              } else {
                otherFiles.add(file.getAbsolutePath());
              }
            }

          });
    }

  }
}
