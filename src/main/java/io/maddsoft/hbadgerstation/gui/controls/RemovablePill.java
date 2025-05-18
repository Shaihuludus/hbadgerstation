package io.maddsoft.hbadgerstation.gui.controls;

import io.maddsoft.hbadgerstation.gui.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class RemovablePill implements Controller {

  @FXML private Label pillLabel;
  @FXML private Button removeButton;

  private PillsController parent;

  public void initialize(String name) {
    pillLabel.setText(name);
    removeButton.setOnAction(_ -> parent.pillButtonPressed(getPillName()));
  }

  public void disableRemoveButton(boolean disable) {
    removeButton.setVisible(!disable);
    removeButton.setDisable(disable);
    removeButton.setManaged(!disable);
  }

  public String getPillName() {
    return pillLabel.getText();
  }

  @Override
  public void setParent(Controller parent) {
    this.parent = (PillsController) parent;
  }
}
