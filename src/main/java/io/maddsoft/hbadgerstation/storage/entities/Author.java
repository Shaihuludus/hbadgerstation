package io.maddsoft.hbadgerstation.storage.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;


@Getter
@Setter
@Builder
@Entity(value = "authors", indices = {
    @Index(fields = "authorName"),
})
public class Author {

  @Id
  private String authorName;

  private String websiteUrl;

  @Override
  public String toString() {
    return authorName;
  }

  public static class AuthorConverter implements EntityConverter<Author> {

    @Override
    public Class<Author> getEntityType() {
      return Author.class;
    }

    @Override
    public Document toDocument(Author entity, NitriteMapper nitriteMapper) {
      return Document.createDocument()
          .put("authorName", entity.getAuthorName())
          .put("websiteUrl", entity.getWebsiteUrl());
    }

    @Override
    public Author fromDocument(Document document, NitriteMapper nitriteMapper) {
      return Author.builder()
          .authorName(document.get("authorName", String.class))
          .websiteUrl(document.get("websiteUrl", String.class))
          .build();
    }
  }
}
