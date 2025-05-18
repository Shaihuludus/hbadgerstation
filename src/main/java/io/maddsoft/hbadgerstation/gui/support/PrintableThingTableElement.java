package io.maddsoft.hbadgerstation.gui.support;

import io.maddsoft.hbadgerstation.cache.ImageCache;
import io.maddsoft.hbadgerstation.gui.GUIDefaults;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.File;
import java.net.MalformedURLException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.collection.NitriteId;

@ToString
@Getter
public class PrintableThingTableElement {

  private final String previewImage;

  private String name;

  private String authorName;

  private NitriteId printableThingId;

  private String directoryPath;

  private String type;

  @Setter
  private int listPosition;

  public PrintableThingTableElement(String imagePath, String name, String authorName, String directoryPath, String type, NitriteId id) {
    this.previewImage = imagePath;
    initialize(name, authorName, directoryPath, type, id);
  }

  public PrintableThingTableElement(String name, String authorName, String directoryPath, String type, NitriteId id) {
      previewImage = StringUtils.EMPTY;
      initialize(name, authorName, directoryPath, type, id);
  }

  private void initialize(String name, String authorName, String directoryPath, String type, NitriteId id){
    this.name =  name;
    this.authorName = authorName;
    this.directoryPath = directoryPath;
    this.type = type;
    this.printableThingId = id;
  }

  public static class PrintableThingTableElementConverter {

    private PrintableThingTableElementConverter() {
    }

    public static PrintableThingTableElement convert(PrintableThing printableThing) {
      if (printableThing.getImages().isEmpty()) {
        return new PrintableThingTableElement(printableThing.getName(), printableThing.getAuthorName(),
            printableThing.getDirectoryPath(), printableThing.getType(), printableThing.getPrintableThingId());
      } else {
        File imageFile = new File(ImageCache.getImageCache().getCachedImage(printableThing.getImages().getFirst(), GUIDefaults.PRINTABLE_IMAGE_MINIATURE_SIZE, GUIDefaults.PRINTABLE_IMAGE_MINIATURE_SIZE));
        try {
          String imageUrl = imageFile.toURI().toURL().toString();
          return new PrintableThingTableElement(imageUrl, printableThing.getName(),
              printableThing.getAuthorName(), printableThing.getDirectoryPath(), printableThing.getType(), printableThing.getPrintableThingId());
        } catch (MalformedURLException _){
          return new PrintableThingTableElement(printableThing.getName(),
              printableThing.getAuthorName(), printableThing.getDirectoryPath(), printableThing.getType(), printableThing.getPrintableThingId());
        }
      }
    }
  }
}
