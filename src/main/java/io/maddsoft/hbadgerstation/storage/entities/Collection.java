package io.maddsoft.hbadgerstation.storage.entities;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteId;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

@Getter
@Setter
@Builder
@Entity
public class Collection {

  @Id
  private NitriteId collectionId;

  private String authorName;

  private List<String> images;

  private String name;

  private List<NitriteId> printableThingIds;

  public String toString() {
    return name;
  }

  public static class CollectionConverter implements EntityConverter<Collection> {

    @Override
    public Class<Collection> getEntityType() {
      return Collection.class;
    }

    @Override
    public Document toDocument(Collection collection, NitriteMapper nitriteMapper) {
      return Document.createDocument()
          .put("id", collection.getCollectionId())
          .put("authorName", collection.getAuthorName())
          .put("images", collection.getImages())
          .put("name", collection.getName())
          .put("printableThingIds", collection.getPrintableThingIds());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection fromDocument(Document document, NitriteMapper nitriteMapper) {
      return Collection.builder()
          .collectionId(document.get("id", NitriteId.class))
          .authorName(document.get("authorName", String.class))
          .images(document.get("images", List.class))
          .name(document.get("name", String.class))
          .printableThingIds(document.get("printableThingIds", List.class) )
          .build();
    }
  }
}
