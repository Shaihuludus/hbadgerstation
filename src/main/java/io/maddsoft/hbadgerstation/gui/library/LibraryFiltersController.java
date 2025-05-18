package io.maddsoft.hbadgerstation.gui.library;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.FilterCollection;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import io.maddsoft.hbadgerstation.storage.entities.Collection;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.util.Comparator;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.Getter;
import org.controlsfx.control.CheckComboBox;

public class LibraryFiltersController implements Controller {

  @FXML private Button cleaFiltersButton;
  @FXML private TextField searchField;
  @FXML private Button searchButton;
  @FXML private CheckComboBox<Author> authorsFilter;

  @FXML private ListView<Collection> collectionList;

  private LibraryViewController parent;

  @Getter
  private final FilterCollection filterCollection = new FilterCollection();

  private final DatabaseManager databaseManager = new DatabaseManager();

  private Boolean collectionsClicked = false;

  public void initialize() {
    authorsFilter.getItems().addAll(prepareAuthorFilters());
    authorsFilter.getCheckModel().getCheckedItems().addListener(
        (ListChangeListener<Author>) change -> {
          while (change.next()) {
            filterCollection.setFilter(Author.AUTHOR_NAME, change.getList().stream().map(
                Author::getAuthorName).toList());
          }
          parent.refreshDataViews();
          refreshCollections();
        });
    collectionList.setOnMouseClicked(_ -> {
      if(Boolean.FALSE.equals(collectionsClicked)) {
        collectionList.getSelectionModel().clearSelection();
        filterCollection.setIdFilter(PrintableThing.PRINTABLE_THING_ID, null);
        parent.refreshDataViews();
        refreshCollections();
      }
      collectionsClicked = false;
    });

    collectionList.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
      collectionsClicked = true;
      if(newValue != null) {
        filterCollection.setIdFilter(PrintableThing.PRINTABLE_THING_ID, newValue.getPrintableThingIds());
      } else {
        filterCollection.setIdFilter(PrintableThing.PRINTABLE_THING_ID, null);
      }
      parent.refreshDataViews();
    });

    searchButton.setOnAction(_ -> {
      filterCollection.setTextFilter(searchField.getText());
      parent.refreshDataViews();
    });

    refreshCollections();
  }

  private void refreshCollections(){
    collectionList.getItems().clear();
    collectionList.getItems().addAll(prepareCollections());
  }

  private List<Collection> prepareCollections(){
    FilterCollection collectionFilters = new FilterCollection();
    collectionFilters.setFilter(Author.AUTHOR_NAME, filterCollection.getFilters().get(Author.AUTHOR_NAME));
    return databaseManager.getCollections(collectionFilters).stream().toList();
  }


  private List<Author> prepareAuthorFilters(){
    return databaseManager.getAuthors().stream().sorted(Comparator.comparing(Author::getAuthorName)).toList();
  }

  public void reloadFilters() {
    authorsFilter.getItems().clear();
    authorsFilter.getItems().addAll(prepareAuthorFilters());

    collectionList.getItems().clear();
    collectionList.getItems().addAll(prepareCollections());
  }

  @Override
  public void setParent(Controller parent) {
    this.parent =(LibraryViewController) parent;
  }
}
