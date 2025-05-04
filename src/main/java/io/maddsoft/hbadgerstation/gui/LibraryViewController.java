package io.maddsoft.hbadgerstation.gui;

import io.maddsoft.hbadgerstation.gui.elements.PrintableThingTableElement;
import io.maddsoft.hbadgerstation.gui.elements.PrintableThingTableElement.PrintableThingTableElementConverter;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LibraryViewController implements Controller{

  @FXML private TableView<PrintableThingTableElement> libraryView;
  @FXML private TableColumn<PrintableThingTableElement, String> nameColumn;
  @FXML private TableColumn<PrintableThingTableElement, String> authorColumn;
  @FXML private TableColumn<PrintableThingTableElement, String> previewColumn;
  @FXML private AnchorPane librarySidebar;

  private final DatabaseManager databaseManager = new DatabaseManager();

  private MainWindowController parent;

  @FXML
  private void initialize() {
    SplitPane.setResizableWithParent(libraryView, false);
    SplitPane.setResizableWithParent(librarySidebar, false);

    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    authorColumn.setCellValueFactory(new PropertyValueFactory<>("authorName"));
    previewColumn.setCellValueFactory(new PropertyValueFactory<>("previewImageView"));
    libraryView.getItems().setAll(databaseManager.getPrintableThings().stream().map(
        PrintableThingTableElementConverter::convert).toList());

    libraryView.getSelectionModel().selectedItemProperty()
        .addListener((_, _, selectedRow) -> parent.activateModeSwitcher(selectedRow != null));
  }

  @Override
  public void setParent(Controller parent) {
    this.parent = (MainWindowController) parent;
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
  public void refreshDataViews() {
    libraryView.getItems().clear();
    libraryView.getItems().addAll(databaseManager.getPrintableThings().stream().map(
        PrintableThingTableElementConverter::convert).toList());
  }
}
