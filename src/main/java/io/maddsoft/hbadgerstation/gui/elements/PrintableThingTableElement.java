package io.maddsoft.hbadgerstation.gui.elements;

import io.maddsoft.hbadgerstation.gui.GUIDefaults;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

@Getter
public class PrintableThingTableElement {

  private final ImageView previewImageView;

  private final String name;

  private final String authorName;

  public PrintableThingTableElement(String imagePath, String name, String authorName) {
    this.name=name;
    this.authorName=authorName;
    previewImageView = new ImageView(new Image(imagePath));
    previewImageView.setFitWidth(GUIDefaults.IMAGE_MINIATURE_WIDTH);
    previewImageView.setPreserveRatio(true);
  }

  public PrintableThingTableElement(String name, String authorName) {
    this.name=name;
    this.authorName=authorName;
    previewImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(GUIDefaults.DEFAULT_IMAGE))));
    previewImageView.setFitWidth(GUIDefaults.IMAGE_MINIATURE_WIDTH);
    previewImageView.setPreserveRatio(true);
  }

  public static class PrintableThingTableElementConverter {

    private PrintableThingTableElementConverter() {
    }

    public static PrintableThingTableElement convert(PrintableThing printableThing) {
      if (printableThing.getImages().isEmpty()) {
        return new PrintableThingTableElement(printableThing.getName(), printableThing.getAuthorName());
      } else {
        File imageFile = new File(printableThing.getImages().getFirst());
        try {
          String imageUrl = imageFile.toURI().toURL().toString();
          return new PrintableThingTableElement(imageUrl, printableThing.getName(),
              printableThing.getAuthorName());
        } catch (MalformedURLException _){
          return new PrintableThingTableElement(printableThing.getName(),
              printableThing.getAuthorName());
        }
      }
    }
  }
}
