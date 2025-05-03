package io.maddsoft.hbadgerstation.gui;

import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.IOException;
import javafx.event.ActionEvent;
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

  @FXML private TableView<PrintableThing> libraryView;
  @FXML private TableColumn<PrintableThing, String> nameColumn;
  @FXML private TableColumn<PrintableThing, String> authorColumn;
  @FXML private AnchorPane librarySidebar;

  private final DatabaseManager databaseManager = new DatabaseManager();

  @FXML
  private void initialize() {
    SplitPane.setResizableWithParent(libraryView, false);
    SplitPane.setResizableWithParent(librarySidebar, false);


    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    authorColumn.setCellValueFactory(new PropertyValueFactory<>("authorName"));
    libraryView.getItems().setAll(databaseManager.getPrintableThings());
  }

  public void openImportPrintableDialog(ActionEvent event) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/importprintable.fxml"));
      Parent parent = fxmlLoader.load();
      Controller controller =fxmlLoader.getController();
      controller.setParent(this);
      Scene scene = new Scene(parent);
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
    libraryView.getItems().addAll(databaseManager.getPrintableThings());
  }
}
