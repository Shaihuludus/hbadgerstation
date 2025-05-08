package io.maddsoft.hbadgerstation.gui;

import io.maddsoft.hbadgerstation.cache.ImageCache;
import java.io.File;
import java.net.MalformedURLException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.StringUtils;

public class ImageViewController implements Controller{

  @FXML private ImageView imageView;
  @FXML private Label imageName;

  public void initialize(String imagePath) throws MalformedURLException {
    File file = new File(ImageCache.getImageCache().getCachedImage(imagePath,GUIDefaults.IMAGE_DISPLAY_SIZE, GUIDefaults.IMAGE_DISPLAY_SIZE));
    imageView.setImage(new Image(file.toURI().toURL().toString(),true));
    imageView.setFitWidth(GUIDefaults.IMAGE_DISPLAY_SIZE);
    imageView.setPreserveRatio(true);
    imageName.setText(StringUtils.substringAfterLast(imagePath.replace("\\","/"), "/"));
  }

}
