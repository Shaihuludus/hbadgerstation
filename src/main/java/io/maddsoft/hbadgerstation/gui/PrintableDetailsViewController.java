package io.maddsoft.hbadgerstation.gui;

import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.collection.NitriteId;

@Slf4j
public class PrintableDetailsViewController implements Controller{

  @FXML private ToggleButton lockButton;
  @FXML private Button revertButton;
  @FXML private Button updateButton;
  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;
  @FXML private TextField authorNameField;
  @FXML private TextField authorWebsiteField;
  @FXML private TextField directoryPathField;
  @FXML private ListView<HBox> imageListView;

  private DatabaseManager databaseManager = new DatabaseManager();

  private PrintableThing printableThing;
  private Author author;

  @FXML
  private void initialize() {
    lockButton.selectedProperty().addListener((_, _, newValue) -> activateEdition(!newValue));
  }

  private void activateEdition(boolean lock) {
    nameField.setEditable(lock);
    descriptionField.setEditable(lock);
    authorWebsiteField.setEditable(lock);
    directoryPathField.setEditable(lock);
  }

  public void setup(NitriteId nitriteId) {
    if( printableThing != null && printableThing.getPrintableThingId().equals(nitriteId)) {
      return;
    }
    printableThing = databaseManager.getPrintableThingById(nitriteId);
    if (printableThing != null) {
      nameField.setText(printableThing.getName());
      nameField.textProperty().addListener((_, _, newValue) -> {
        updateButton.setDisable(printableThing.getName().equals(newValue));
        revertButton.setDisable(printableThing.getName().equals(newValue));
      });
      descriptionField.setText(printableThing.getDescription());
      directoryPathField.setText(printableThing.getDirectoryPath());
      setupAuthorTabPane(printableThing.getAuthorName());
      setupImageTab();
    }
  }

  private void setupImageTab() {
    imageListView.getItems().clear();
    HBox rowBox = setupImageBox();
    rowBox.setAlignment(Pos.CENTER);
    for (String imageName : printableThing.getImages())  {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/imageView.fxml"));
      try {

        VBox imageViewRoot = fxmlLoader.load();
        ImageViewController imageViewController = fxmlLoader.getController();
        imageViewController.initialize(imageName);
        imageViewRoot.prefWidth(GUIDefaults.IMAGE_DISPLAY_WIDTH);
        rowBox.getChildren().add(imageViewRoot);
        HBox.setMargin(imageViewRoot, new Insets(10));
        if(rowBox.getChildren().size() == GUIDefaults.IMAGE_GRID_COLUMNS) {
          imageListView.getItems().add(rowBox);
          rowBox = setupImageBox();
          rowBox.setAlignment(Pos.CENTER);
        }

      } catch (IOException e) {
        log.error(e.getMessage());
      }
    }
    if(!rowBox.getChildren().isEmpty() ) {
      imageListView.getItems().add(rowBox);
    }
  }

  private void setupAuthorTabPane(String authorName) {
    author = databaseManager.getAuthorByName(authorName);
    if (author != null) {
      authorNameField.setText(author.getAuthorName());
      authorWebsiteField.setText(author.getWebsiteUrl());
    }
  }

  private HBox setupImageBox() {
    HBox rowBox = new HBox();
    rowBox.setAlignment(Pos.CENTER);
    return rowBox;
  }

}
