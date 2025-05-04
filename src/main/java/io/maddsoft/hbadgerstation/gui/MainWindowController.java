package io.maddsoft.hbadgerstation.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainWindowController implements Controller {

  @FXML private VBox mainViewBox;

  @FXML private ToggleGroup modeSwitcher;

  @FXML
  private void initialize() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/libraryview.fxml"));
    SplitPane libraryViewPane = fxmlLoader.load();
    Controller controller =fxmlLoader.getController();
    controller.setParent(this);
    mainViewBox.getChildren().add(1,libraryViewPane);
    VBox.setVgrow(libraryViewPane, Priority.ALWAYS);
  }

  public void activateModeSwitcher(boolean actvate) {
    if (actvate) {
      modeSwitcher.getToggles().forEach(
          toggle -> ((ToggleButton)toggle).setDisable(false)
      );
    } else {
      modeSwitcher.getToggles().forEach(
          toggle -> ((ToggleButton) toggle).setDisable(true)
      );
    }
  }
}
