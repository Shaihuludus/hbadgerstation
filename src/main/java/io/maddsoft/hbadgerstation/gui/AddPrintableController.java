package io.maddsoft.hbadgerstation.gui;

import io.maddsoft.hbadgerstation.gui.library.LibraryViewController;
import io.maddsoft.hbadgerstation.storage.AuthorImporter;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.ModelImporter;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class AddPrintableController implements Controller{

  private Controller parent;

  @FXML private TextField pathField;
  @FXML private TextField authorField;
  @FXML private Button importButton;
  @FXML private CheckBox rootDirectoryCheckbox;
  @FXML private CheckBox collectionCheckbox;
  @FXML private ComboBox<String> authorCombo;

  @FXML
  private void initialize() {
    pathField.textProperty().addListener((_, _, newValue) -> importButton.setDisable(!StringUtils.isNotBlank(newValue)));
    rootDirectoryCheckbox.selectedProperty().addListener((_, _, newValue) -> {
      collectionCheckbox.setDisable(!newValue);
      if(Boolean.FALSE.equals(newValue)) {
        collectionCheckbox.setSelected(false);
      }
    });
    authorField.textProperty().addListener((_, _, newValue) -> initializeAuthors());
    authorCombo.valueProperty().addListener((_, _, newValue) -> authorField.setText(newValue));
    initializeAuthors();
  }



  private void initializeAuthors() {
    DatabaseManager databaseManager = new DatabaseManager();
    authorCombo.getItems().clear();
    authorCombo.getItems().addAll(databaseManager
        .getAuthors()
        .stream()
        .map(Author::getAuthorName)
        .filter(authorName -> authorName == null || authorName.toLowerCase().startsWith(this.authorField.getText().toLowerCase())).toList());

  }


  @Override
  public void setParent(Controller parent) {
    this.parent = parent;
  }

  public void openFileChooser() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select a directory");
    if (parent instanceof LibraryViewController libraryViewController){
      File currentDirectory = libraryViewController.getCurrentDirectory();
      if (currentDirectory != null){
        directoryChooser.setInitialDirectory(currentDirectory);
      }
    }
    File chosenDirectory = directoryChooser.showDialog(pathField.getScene().getWindow());
    if (chosenDirectory != null) {
      pathField.setText(chosenDirectory.getAbsolutePath());
    }
  }

  public void importPrintableAction(){
    String authorName = StringUtils.isNotBlank(authorField.textProperty().getValue()) ? authorField.textProperty().getValue().trim() : AuthorImporter.DEFAULT_AUTHOR_NAME;
    // The path field is editable, so it is the source of truth (it may differ from the chooser result).
    String path = StringUtils.trimToEmpty(pathField.getText());
    File directory = new File(path);
    if (path.isEmpty() || !directory.isDirectory()) {
      String message = directory.exists() ? "Not a directory:\n" : "Directory does not exist:\n";
      Alert alert = new Alert(AlertType.ERROR, message + path);
      alert.initOwner(importButton.getScene().getWindow());
      alert.showAndWait();
      return;
    }
    ModelImporter importer = new ModelImporter(directory, authorName, rootDirectoryCheckbox.isSelected(), collectionCheckbox.isSelected());
    importer.importModels();
    parent.refreshDataViews();
    if (parent instanceof LibraryViewController libraryViewController) {
      libraryViewController.setCurrentDirectory(directory);
      libraryViewController.refreshAuthors();
      libraryViewController.selectPrintable(importer.getLastImportedId());
    }
    ((Stage) importButton.getScene().getWindow()).close();
  }
}
