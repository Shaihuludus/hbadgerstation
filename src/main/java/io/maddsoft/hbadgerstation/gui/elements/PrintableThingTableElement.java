package io.maddsoft.hbadgerstation.gui.elements;

import io.maddsoft.hbadgerstation.gui.GUIDefaults;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import org.dizitart.no2.collection.NitriteId;

@Getter
public class PrintableThingTableElement {

  private final ImageView previewImageView;

  private String name;

  private String authorName;

  private NitriteId printableThingId;

  private String directoryPath;

  private String type;

  public PrintableThingTableElement(String imagePath, String name, String authorName, String directoryPath, String type, NitriteId id) {
    previewImageView = new ImageView(new Image(imagePath));
    previewImageView.setFitWidth(GUIDefaults.IMAGE_MINIATURE_WIDTH);
    previewImageView.setPreserveRatio(true);
    initialize(name, authorName, directoryPath, type, id);
  }

  public PrintableThingTableElement(String name, String authorName, String directoryPath, String type, NitriteId id) {
      previewImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(GUIDefaults.DEFAULT_IMAGE))));
      previewImageView.setFitWidth(GUIDefaults.IMAGE_MINIATURE_WIDTH);
      previewImageView.setPreserveRatio(true);
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
        File imageFile = new File(printableThing.getImages().getFirst());
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
