package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.gui.MainWindowController;
import io.maddsoft.hbadgerstation.gui.elements.PrintableThingTableElement;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Region;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.dizitart.no2.collection.NitriteId;

@Slf4j
public class PrintableDetailsViewController implements Controller {

  @FXML private TabPane tabPane;
  @FXML private ToggleButton lockButton;
  @FXML private Button revertButton;
  @FXML private Button updateButton;
  @FXML private Tab imagesTab;
  @FXML private Tab printableTab;
  @FXML private Tab authorTab;
  @FXML private Tab generalTab;


  private AuthorTabController authorTabController;
  private GeneralTabController generalTabController;
  private final DatabaseManager databaseManager = new DatabaseManager();

  private PrintableThing printableThing;

  private MainWindowController parent;

  public void setup(NitriteId nitriteId) {
    if( printableThing != null && printableThing.getPrintableThingId().equals(nitriteId)) {
      return;
    }
    printableThing = databaseManager.getPrintableThingById(nitriteId);
    if(printableThing != null) {
      updateButton.setOnAction(this::updatePrintable);
      try {
        setupGeneralTab();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
      try {
        setupAuthorTabPane();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
      try {
        setupImageTab();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
      try {
        setupPrintableTab();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
    activateEdition(false);
    updateButton.setDisable(true);
    revertButton.setDisable(true);
    tabPane.getSelectionModel().select(0);
  }

  @Override
  public void setParent(Controller parent) {
    this.parent = (MainWindowController) parent;
  }

  public void changeHappened() {
    revertButton.setDisable(false);
    updateButton.setDisable(false);
  }

  public void switchSelectedPrintableThing(PrintableThingTableElement selectedItem) {
    parent.switchSeletedItem(selectedItem);
    tabPane.getSelectionModel().select(0);
  }

  @FXML
  private void initialize() {
    lockButton.selectedProperty().addListener((_, _, newValue) -> activateEdition(!newValue));

  }

  private void updatePrintable(ActionEvent actionEvent) {
    generalTabController.updatePrintableThing();
    boolean isNewAuthor = authorTabController.isNewAuthor();
    Author author = authorTabController.updateAuthor();
    printableThing.setAuthorName(author.getAuthorName());
    if(isNewAuthor) {
      databaseManager.addAuthor(author);
    }
    databaseManager.updatePrintableThing(printableThing);
    revertButton.setDisable(true);
    updateButton.setDisable(true);
    parent.refreshLibraryView();
  }

  private void activateEdition(boolean lock) {
    lockButton.setGraphic(!lock ? new Glyph("FontAwesome", FontAwesome.Glyph.LOCK) : new Glyph("FontAwesome", FontAwesome.Glyph.UNLOCK));
    authorTabController.setEditable(lock);
    generalTabController.setEditable(lock);
  }

  private void setupGeneralTab() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/details/generaltab.fxml"));
    Region view = fxmlLoader.load();
    generalTabController = fxmlLoader.getController();
    generalTabController.initialize(printableThing);
    generalTabController.setParent(this);
    generalTab.setContent(view);
  }

  private void setupPrintableTab() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/details/printablestab.fxml"));
    Region view = fxmlLoader.load();
    PrintableTabController printableTabController = fxmlLoader.getController();
    printableTabController.initialize(printableThing.getPrintFilenames(), StringUtils.stripEnd(printableThing.getDirectoryPath().replace("\\", "/"), "/"));
    printableTab.setContent(view);
  }

  private void setupImageTab() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/details/imagestab.fxml"));
    Region view = fxmlLoader.load();
    ImageTabController imagesTabController = fxmlLoader.getController();
    imagesTabController.initialize(printableThing.getImages());
    imagesTab.setContent(view);
  }

  private void setupAuthorTabPane() throws IOException {
    Author author = databaseManager.getAuthorByName(printableThing.getAuthorName());
    if (author != null) {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/details/authortab.fxml"));
      Region view = fxmlLoader.load();
      authorTabController = fxmlLoader.getController();
      authorTabController.initialize(author, printableThing.getPrintableThingId());
      authorTabController.setParent(this);
      authorTab.setContent(view);
    }
  }

}
