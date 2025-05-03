package io.maddsoft.hbadgerstation.storage;

import io.maddsoft.hbadgerstation.storage.entities.Author;

public class AuthorImporter {

  public final static String DEFAULT_AUTHOR_NAME = "unknown";

  private final  DatabaseManager databaseManager = new DatabaseManager();

  private final String authorName;

  private final String websiteUrl;

  public AuthorImporter(String authorName, String websiteUrl) {
    this.authorName = authorName;
    this.websiteUrl = websiteUrl;
  }

  public void importAuthor() {
    if (databaseManager.getAuthorByName(authorName) == null) {
      databaseManager.addAuthor(Author.builder()
          .authorName(authorName)
          .websiteUrl(websiteUrl)
          .build());
    };
  }
}
