package io.maddsoft.hbadgerstation.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

@Getter
@Setter
@AllArgsConstructor
@Entity(value = "tags", indices = {
    @Index(fields = "tag"),
})
public class TagEntity {

  @Id
  private String tag;

  public String toString() {
    return tag;
  }

  public static class TagConverter implements EntityConverter<TagEntity> {

    @Override
    public Class<TagEntity> getEntityType() {
      return TagEntity.class;
    }

    @Override
    public Document toDocument(TagEntity entity, NitriteMapper nitriteMapper) {
      return Document.createDocument()
          .put("tag", entity.getTag());
    }

    @Override
    public TagEntity fromDocument(Document document, NitriteMapper nitriteMapper) {
      return new TagEntity(document.get("tag", String.class));
    }
  }
}
