package io.maddsoft.hbadgerstation.gui;

import java.io.File;
import java.net.MalformedURLException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewController implements Controller{

  @FXML private ImageView imageView;
  @FXML private Label imageName;

  public void initialize(String imagePath) throws MalformedURLException {
    File file = new File(imagePath);
    imageView.setImage(new Image(file.toURI().toURL().toString(),true));
    imageView.setFitWidth(GUIDefaults.IMAGE_DISPLAY_WIDTH);
    imageView.setPreserveRatio(true);
    imageName.setText(file.getName());
  }

}
