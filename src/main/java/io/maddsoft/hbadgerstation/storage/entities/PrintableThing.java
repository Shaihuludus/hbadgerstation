package io.maddsoft.hbadgerstation.storage.entities;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteId;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

@Getter
@Setter
@ToString
@Builder
@Entity(indices = {
    @Index(fields = {"searchIndex"}, type = IndexType.FULL_TEXT)
})
public class PrintableThing {

  public static final String PRINTABLE_THING_ID = "printableThingId";

  public static final List<String> PRINTABLE_THING_TYPES = List.of("Resin", "FDM", "Resin and FDM");

  @Id
  private NitriteId printableThingId;

  private String name;

  private String description;

  private String authorName;

  private List<String> printFilenames;

  private List<String> images;

  private List<String> otherFiles;

  private String type;

  private List<String> tags;

  private Boolean directory;

  private String directoryPath;

  private String searchIndex;

  public static class PrintableConverter implements EntityConverter<PrintableThing> {

    @Override
    public Class<PrintableThing> getEntityType() {
      return PrintableThing.class;
    }

    @Override
    public Document toDocument(PrintableThing entity, NitriteMapper nitriteMapper) {
      return Document.createDocument()
          .put(PRINTABLE_THING_ID, entity.getPrintableThingId())
          .put("name", entity.getName())
          .put("description", entity.getDescription())
          .put("authorName", entity.getAuthorName())
          .put("printFilenames", entity.getPrintFilenames())
          .put("images", entity.getImages())
          .put("otherFiles", entity.getOtherFiles())
          .put("type", entity.getType())
          .put("tags", entity.getTags())
          .put("directory", entity.getDirectory())
          .put("directoryPath", entity.getDirectoryPath())
          .put("searchIndex", entity.getName() + " " + entity.getAuthorName() + " " + tagsToString(entity.getTags()));
    }

    private String tagsToString(List<String> tags) {
      if (tags != null && !tags.isEmpty()) {
        StringBuilder tagsString  = new StringBuilder();
        tags.forEach(tag -> tagsString.append(tag).append(" "));
        return tagsString.toString();
      }
      return "";
    }

    @SuppressWarnings("unchecked")
    @Override
    public PrintableThing fromDocument(Document document, NitriteMapper nitriteMapper) {
      return PrintableThing.builder()
          .printableThingId(document.get(PRINTABLE_THING_ID, NitriteId.class))
          .name(document.get("name", String.class))
          .description(document.get("description", String.class))
          .authorName(document.get("authorName", String.class))
          .printFilenames(document.get("printFilenames", List.class))
          .images(document.get("images", List.class))
          .type(document.get("type", String.class))
          .otherFiles(document.get("otherFiles", List.class))
          .tags(document.get("tags", List.class))
          .directory(document.get("directory", Boolean.class))
          .directoryPath(document.get("directoryPath", String.class))
          .build();
    }
  }
}
