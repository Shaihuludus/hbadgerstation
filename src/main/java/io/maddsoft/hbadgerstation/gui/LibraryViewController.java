package io.maddsoft.hbadgerstation.gui;

import io.maddsoft.hbadgerstation.gui.elements.PrintableThingTableElement;
import io.maddsoft.hbadgerstation.gui.elements.PrintableThingTableElement.PrintableThingTableElementConverter;
import io.maddsoft.hbadgerstation.gui.gridview.CustomGridViewSkin;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellController;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellSelectionController;
import io.maddsoft.hbadgerstation.gui.printableview.PrintableViewController;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewSelectManager;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

@Slf4j
public class LibraryViewController implements GridCellSelectionController {

  @FXML private Button deleteButton;
  @FXML private GridView<VBox> libraryView;
  @FXML private VBox librarySidebar;
  @FXML private Button openExplorerButton ;
  @FXML private AnchorPane libraryParent;

  private final DatabaseManager databaseManager = new DatabaseManager();

  private MainWindowController parent;

  private PrintableThingTableElement selectedItem;

  private final GridViewSelectManager gridViewSelectManager = new GridViewSelectManager();

  @Getter
  @Setter
  private File currentDirectory;

  @FXML
  private void initialize() {
    gridViewSelectManager.addGridControllerToNotify(this);
    SplitPane.setResizableWithParent(librarySidebar, false);

    libraryView.setItems(FXCollections.observableArrayList(prepareLibraryView()));
    SplitPane.setResizableWithParent(libraryView, false);
    libraryView.setCellHeight(400);
    libraryView.setCellWidth(400);
    libraryView.setSkin(new CustomGridViewSkin<>(libraryView));
    libraryView.setCellFactory(new GridViewGridCellCallback());
    prepareLibraryView();
  }

  private List<VBox> prepareLibraryView() {
    AtomicInteger tempPosition = new AtomicInteger();
    return databaseManager.getPrintableThings().stream()
        .map(PrintableThingTableElementConverter::convert).map(printableThingTableElement -> {
          FXMLLoader fxmlLoader = new FXMLLoader();
          fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/printableView.fxml"));
          try {
            VBox imageViewRoot = fxmlLoader.load();
            printableThingTableElement.setListPosition(tempPosition.getAndIncrement());
            PrintableViewController imageViewController = fxmlLoader.getController();
            imageViewRoot.setOnMouseClicked(imageViewController::onMouseClicked);
            imageViewController.initialize(printableThingTableElement, gridViewSelectManager);
            imageViewRoot.prefWidth(GUIDefaults.IMAGE_DISPLAY_SIZE);
            return imageViewRoot;
            } catch (IOException e) {
            log.error(e.getMessage());
          }
          return null;
        }).toList();

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

  @Override
  public void refreshDataViews() {
    libraryView.getItems().clear();
    libraryView.setItems(FXCollections.observableArrayList(prepareLibraryView()));
  }

  @Override
  public void selectedCell(GridCellController controller) {
    if (controller == null) {
      selectedItem = null;
    }  else {
      selectedItem = ((PrintableViewController) controller).getPrintableThingTableElement();
    }
    parent.activateModeSwitcher(selectedItem != null);
    deleteButton.setDisable(selectedItem == null);
    if (selectedItem != null) {
      parent.setNitriteId(selectedItem.getPrintableThingId());
    }
  }

  public void deletePrintable() {
    databaseManager.deletePrintableThing(selectedItem.getPrintableThingId());
    refreshDataViews();
    ObservableList<VBox> items = libraryView.getItems();
    if(!items.isEmpty()){
      EventsCreator.fireMouseClicked(libraryView.getItems().get(selectedItem.getListPosition() < items.size() ? selectedItem.getListPosition() : selectedItem.getListPosition()-1 ));
      Event.fireEvent(libraryView, new ScrollToEvent(libraryView, libraryView, ScrollToEvent.scrollToTopIndex(), libraryView.getItems().size()));
    } else {
      gridViewSelectManager.setPrintableViewSelected(null);
    }

  }

  private static class GridViewGridCellCallback implements
      Callback<GridView<VBox>, GridCell<VBox>> {

    @Override
    public GridCell<VBox> call(
        GridView<VBox> printableThingTableElementGridView) {
      return new GridCell<>() {

        @Override
        public void updateIndex(int i) {
          if (i != -1) {
            super.updateIndex(i);
          }
        }

        @Override
        protected void updateItem(VBox vbox, boolean empty) {
          super.updateItem(vbox, empty);
          if (empty) {
            setText(null);
            setGraphic(null);
          } else if (vbox != null) {
            setText(null);
            setGraphic(vbox);
          } else {
            setText("null");
            setGraphic(null);
          }
        }
      };
    }
  }
}
