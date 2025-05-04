package io.maddsoft.hbadgerstation.gui;

import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.dizitart.no2.collection.NitriteId;

public class PrintableDetailsViewController implements Controller{

  @FXML private ToggleButton lockButton;
  @FXML private Button revertButton;
  @FXML private Button updateButton;
  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;

  private DatabaseManager databaseManager = new DatabaseManager();

  private PrintableThing printableThing;

  @FXML
  private void initialize() {
    lockButton.selectedProperty().addListener((_, _, newValue) -> activateEdition(newValue));
  }

  private void activateEdition(boolean lock) {
    nameField.setDisable(lock);
    descriptionField.setDisable(lock);
  }

  public void setup(NitriteId nitriteId) {
    printableThing = databaseManager.getPrintableThingById(nitriteId);
    if (printableThing != null) {
      nameField.setText(printableThing.getName());
      nameField.textProperty().addListener((_, _, newValue) -> {
        updateButton.setDisable(printableThing.getName().equals(newValue));
        revertButton.setDisable(printableThing.getName().equals(newValue));
      });
      descriptionField.setText(printableThing.getDescription());
    }
  }

}
