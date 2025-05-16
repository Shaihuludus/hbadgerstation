package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class GeneralTabController implements Controller {

  @FXML private TextField typeField;
  @FXML private ComboBox<String> typeSelectBox;
  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;
  @FXML private TextField directoryPathField;

  private PrintableDetailsViewController parent;

  private PrintableThing printableThing;

  public void initialize(PrintableThing printableThing) {
    this.printableThing = printableThing;
    typeSelectBox.getItems().addAll(PrintableThing.PRINTABLE_THING_TYPES);
    if (this.printableThing != null) {
      nameField.setText(this.printableThing.getName());
      nameField.textProperty().addListener((_, _, newValue) -> {

        if (!Objects.equals(this.printableThing.getName(), newValue)){
          parent.changeHappened();
        }
      });
      descriptionField.setText(this.printableThing.getDescription());
      descriptionField.textProperty().addListener((_, _, newValue) -> {
        if (!Objects.equals(this.printableThing.getDescription(), newValue)){
          parent.changeHappened();
        }
      });
      directoryPathField.setText(this.printableThing.getDirectoryPath());
      typeField.setText(this.printableThing.getType());
      if (StringUtils.isNotBlank(this.printableThing.getType())) {
        typeSelectBox.getSelectionModel().select(this.printableThing.getType());
      }
      typeSelectBox.valueProperty().addListener((_, _, newValue) -> {
        if (!Objects.equals(this.printableThing.getType(), newValue)){
          typeField.setText(newValue);
          parent.changeHappened();
        }
      });
    }
  }

  @Override
  public void setParent(Controller parent) {
    this.parent = (PrintableDetailsViewController) parent;
  }

  public void setEditable(boolean lock) {
    nameField.setEditable(lock);
    descriptionField.setEditable(lock);
    directoryPathField.setEditable(lock);
    if (lock) {
      typeSelectBox.setDisable(false);
      typeSelectBox.setVisible(true);
      typeField.setVisible(false);
      typeField.setManaged(false);
    } else {
      typeSelectBox.setDisable(true);
      typeSelectBox.setVisible(false);
      typeField.setVisible(true);
      typeField.setManaged(true);
    }
  }

  public void updatePrintableThing() {
    printableThing.setName(nameField.getText());
    printableThing.setDescription(descriptionField.getText());
    printableThing.setDirectoryPath(directoryPathField.getText());
    printableThing.setType(typeField.getText());
  }
}
