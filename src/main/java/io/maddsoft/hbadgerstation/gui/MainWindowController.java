package io.maddsoft.hbadgerstation.gui;

import io.maddsoft.hbadgerstation.gui.details.PrintableDetailsViewController;
import io.maddsoft.hbadgerstation.gui.support.PrintableThingTableElement;
import io.maddsoft.hbadgerstation.gui.library.LibraryViewController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.collection.NitriteId;

@Slf4j
public class MainWindowController implements Controller {

  @Getter
  @Setter
  private NitriteId nitriteId;

  @FXML private VBox mainViewBox;

  @FXML private ToggleGroup modeSwitcher;

  @Getter
  @FXML private ToggleButton detailsModeButton;

  @FXML private ToggleButton libraryModeButton;

  private Region libraryView;
  private Region detailsView;

  private PrintableDetailsViewController detailsViewController;

  private LibraryViewController libraryViewController;


  public void activateModeSwitcher(boolean activate) {
    if (activate) {
      modeSwitcher.getToggles().forEach(
          toggle -> ((ToggleButton)toggle).setDisable(false)
      );
    } else {
      modeSwitcher.getToggles().forEach(
          toggle -> ((ToggleButton) toggle).setDisable(true)
      );
    }
  }

  public void refreshLibraryView() {
    libraryViewController.refreshDataViews();
    libraryViewController.refreshAuthors();
  }

  public void postConstruct() {
    libraryViewController.postConstruct();
  }

  public void switchSeletedItem(PrintableThingTableElement selectedItem) {
    nitriteId = selectedItem.getPrintableThingId();
    try {
      detailsViewActivated();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  @FXML
  private void initialize() throws IOException {
    libraryViewActivated();
    modeSwitcher.selectedToggleProperty().addListener((_, oldVal, newVal) -> {
      if (newVal == null)
        oldVal.setSelected(true);
    });
    libraryModeButton.selectedProperty().addListener((_, _, newValue) -> {
      if (Boolean.TRUE.equals(newValue)) {
        try {
          libraryViewActivated();
        } catch (IOException e) {
          log.error(e.getMessage());
        }
      }
    });
    detailsModeButton.selectedProperty().addListener((_, _, newValue) -> {
      if (Boolean.TRUE.equals(newValue)) {
        try {
          detailsViewActivated();
        } catch (IOException e) {
          log.error(e.getMessage());
        }
      }
    });
  }

  private void libraryViewActivated() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/libraryview.fxml"));
    if( libraryView == null ) {
      libraryView = createView(fxmlLoader);
    }
    if (libraryViewController == null) {
      libraryViewController = fxmlLoader.getController();
    }
    if (nitriteId != null && libraryViewController.getSelectedItem() != null && !nitriteId.equals(libraryViewController.getSelectedItem().getPrintableThingId())) {
      nitriteId = libraryViewController.getSelectedItem().getPrintableThingId();
    }
    addToView(libraryView);
  }

  private void detailsViewActivated() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/printabledetailsview.fxml"));
    if (detailsView == null ) {
      detailsView = createView(fxmlLoader);
    }
    if (detailsViewController == null) {
      detailsViewController = fxmlLoader.getController();
      detailsViewController.setParent(this);
    }
    detailsViewController.setup(nitriteId);
    addToView(detailsView);
  }

  private Region createView(FXMLLoader fxmlLoader) throws IOException {
    Region view = fxmlLoader.load();
    ((Controller) fxmlLoader.getController()).setParent(this);
    return view;
  }

  private void addToView(Region view) {
    if (mainViewBox.getChildren().size()>2) {
      mainViewBox.getChildren().remove(1);
    }
    mainViewBox.getChildren().add(1, view);
    VBox.setVgrow(view, Priority.ALWAYS);
  }
}
