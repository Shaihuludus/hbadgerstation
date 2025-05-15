package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneralTabController implements Controller {

  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;
  @FXML private TextField directoryPathField;

  private PrintableDetailsViewController parent;

  private PrintableThing printableThing;

  public void initialize(PrintableThing printableThing) {
    this.printableThing = printableThing;
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
  }

  public void updatePrintableThing() {
    printableThing.setName(nameField.getText());
    printableThing.setDescription(descriptionField.getText());
    printableThing.setDirectoryPath(directoryPathField.getText());
  }
}
