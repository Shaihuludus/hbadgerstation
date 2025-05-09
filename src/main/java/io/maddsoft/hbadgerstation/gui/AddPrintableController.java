package io.maddsoft.hbadgerstation.gui;

import io.maddsoft.hbadgerstation.gui.gridview.GridCellSelectionController;
import io.maddsoft.hbadgerstation.storage.AuthorImporter;
import io.maddsoft.hbadgerstation.storage.ModelImporter;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

  private File chosenDirectory;

  @FXML
  private void initialize() {
    pathField.textProperty().addListener((_, _, newValue) -> importButton.setDisable(!StringUtils.isNotBlank(newValue)));
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
    chosenDirectory = directoryChooser.showDialog(pathField.getScene().getWindow());
    if (chosenDirectory != null) {
      pathField.setText(chosenDirectory.getAbsolutePath());
    }
  }

  public void importPrintableAction(){
    String authorName = StringUtils.isNotBlank(authorField.textProperty().getValue()) ? authorField.textProperty().getValue().trim() : AuthorImporter.DEFAULT_AUTHOR_NAME;
    if (chosenDirectory != null) {
      if (parent instanceof LibraryViewController libraryViewController){
        libraryViewController.setCurrentDirectory(chosenDirectory);
      }
      new ModelImporter(chosenDirectory, authorName, rootDirectoryCheckbox.isSelected()).importModels();
      parent.refreshDataViews();
      if (parent instanceof GridCellSelectionController gridCellSelectionController){
        gridCellSelectionController.selectLastCell();
        gridCellSelectionController.scrollToLastCell();
      }
      ((Stage) importButton.getScene().getWindow()).close();
    }
  }
}
