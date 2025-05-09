package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Region;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.collection.NitriteId;

@Slf4j
public class PrintableDetailsViewController implements Controller {

  @FXML private ToggleButton lockButton;
  @FXML private Button revertButton;
  @FXML private Button updateButton;
  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;
  @FXML private TextField authorNameField;
  @FXML private TextField authorWebsiteField;
  @FXML private TextField directoryPathField;

  private DatabaseManager databaseManager = new DatabaseManager();

  private PrintableThing printableThing;
  private Author author;

  @FXML private Tab imagesTab;

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
      try {
        setupImageTab();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  private void setupImageTab() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/details/imagestab.fxml"));
    Region view = fxmlLoader.load();
    ImageTabController imagesTabController = fxmlLoader.getController();
    imagesTabController.initialize(printableThing.getImages());
    imagesTab.setContent(view);
  }

  private void setupAuthorTabPane(String authorName) {
    author = databaseManager.getAuthorByName(authorName);
    if (author != null) {
      authorNameField.setText(author.getAuthorName());
      authorWebsiteField.setText(author.getWebsiteUrl());
    }
  }

}
