package io.maddsoft.hbadgerstation.gui.library;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.gui.EventsCreator;
import io.maddsoft.hbadgerstation.gui.MainWindowController;
import io.maddsoft.hbadgerstation.gui.gridview.CustomGridViewSkin;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellController;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewGridCellCallback;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewPrintableBuilder;
import io.maddsoft.hbadgerstation.gui.gridview.PrintableThingGridSelectionController;
import io.maddsoft.hbadgerstation.gui.support.PrintableThingTableElement;
import io.maddsoft.hbadgerstation.storage.FilterCollection;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.GridView;
import org.dizitart.no2.collection.NitriteId;

@Slf4j
public class LibraryViewController extends PrintableThingGridSelectionController {

  @FXML private Button deleteButton;
  @FXML private GridView<VBox> libraryView;
  @FXML private VBox librarySidebar;
  @FXML private Button openExplorerButton ;

  private MainWindowController parent;

  private LibraryFiltersController filtersController;

  @Getter
  @Setter
  private File currentDirectory;

  private final GridViewPrintableBuilder gridViewBuilder = new GridViewPrintableBuilder();

  @FXML
  private void initialize() {
    gridViewSelectManager.addGridControllerToNotify(this);
    libraryView.setItems(FXCollections.observableArrayList(gridViewBuilder.buildPrintable(new FilterCollection(), gridViewSelectManager, true)));
    libraryView.setCellHeight(400);
    libraryView.setCellWidth(400);
    libraryView.setSkin(new CustomGridViewSkin<>(libraryView));
    libraryView.setCellFactory(new GridViewGridCellCallback());
    setupFilters();
  }

  private void setupFilters() {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/libraryfiltersview.fxml"));
    try {
      librarySidebar.getChildren().add(fxmlLoader.load());
      filtersController = fxmlLoader.getController();
      filtersController.setParent(this);

    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  @Override
  public void setParent(Controller parent) {
    this.parent = (MainWindowController) parent;
  }

  public void openExplorerAction() throws IOException {
    if (selectedItem != null) {
      Desktop.getDesktop().open(new File(selectedItem.getDirectoryPath()));
    }
  }

  public void openImportPrintableDialog() {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/importprintable.fxml"));
      Parent view = fxmlLoader.load();
      Controller controller =fxmlLoader.getController();
      controller.setParent(this);
      Scene scene = new Scene(view);
      Stage stage = new Stage();
      stage.setResizable(false);
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      log.error("Failed to create new Window.", e);
    }
  }

  @Override
  public void performAction() {
    parent.getDetailsModeButton().setSelected(true);
  }

  /**
   * Rebuilds the grid. The previous selection is restored when the printable is still visible,
   * otherwise it is cleared, so selectedItem never points at a cell of the old grid.
   */
  @Override
  public void refreshDataViews() {
    NitriteId previouslySelected = selectedItem == null ? null : selectedItem.getPrintableThingId();
    libraryView.getItems().clear();
    libraryView.setItems(FXCollections.observableArrayList(gridViewBuilder.buildPrintable(filtersController.getFilterCollection(), gridViewSelectManager, true)));
    if (!selectPrintable(previouslySelected)) {
      gridViewSelectManager.setPrintableViewSelected(null);
    }
  }

  public void refreshAuthors() {
    filtersController.reloadFilters();
  }

  @Override
  public void selectedCell(GridCellController controller) {
    super.selectedCell(controller);
    parent.activateModeSwitcher(selectedItem != null);
    deleteButton.setDisable(selectedItem == null);
    if (selectedItem != null) {
      parent.setNitriteId(selectedItem.getPrintableThingId());
    }
  }

  public void deletePrintable() {
    if (selectedItem == null) {
      return;
    }
    // Capture before refresh: refreshDataViews() clears the selection because the item is gone.
    int deletedPosition = selectedItem.getListPosition();
    databaseManager.deletePrintableThing(selectedItem.getPrintableThingId());
    refreshDataViews();
    ObservableList<VBox> items = libraryView.getItems();
    if (!items.isEmpty()) {
      int nextPosition = Math.min(deletedPosition, items.size() - 1);
      EventsCreator.fireMouseClicked(items.get(nextPosition));
      scrollToPosition(nextPosition);
    }
  }

  /**
   * Selects and scrolls to the cell showing the given printable.
   *
   * @return false when the id is null or the printable is not in the current (possibly filtered) view
   */
  public boolean selectPrintable(NitriteId printableThingId) {
    if (printableThingId == null) {
      return false;
    }
    for (VBox item : libraryView.getItems()) {
      if (item != null
          && item.getUserData() instanceof PrintableThingTableElement element
          && printableThingId.equals(element.getPrintableThingId())) {
        EventsCreator.fireMouseClicked(item);
        scrollToPosition(element.getListPosition());
        return true;
      }
    }
    return false;
  }

  private void scrollToPosition(int listPosition) {
    EventsCreator.fireScrollToIndexEvent(libraryView,
        calculateItemIndex((CustomGridViewSkin<?>) libraryView.getSkin(), listPosition));
  }

  public void scrollToFirstCell() {
    EventsCreator.fireScrollToIndexEvent(libraryView,0);
  }

  public void postConstruct() {
    if (!libraryView.getItems().isEmpty()) {
      EventsCreator.fireMouseClicked(libraryView.getItems().getFirst());
      scrollToFirstCell();
    }
  }
}
