package io.maddsoft.hbadgerstation.gui.library;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.FilterCollection;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import java.util.Comparator;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import lombok.Getter;
import org.controlsfx.control.CheckComboBox;

public class LibraryFiltersController implements Controller {

  @FXML private CheckComboBox<Author> authorsFilter;

  private LibraryViewController parent;

  @Getter
  private final FilterCollection filterCollection = new FilterCollection();

  private final DatabaseManager databaseManager = new DatabaseManager();

  public void initialize() {
    authorsFilter.getItems().addAll(prepareAuthorFilters());
    authorsFilter.getCheckModel().getCheckedItems().addListener(
        (ListChangeListener<Author>) change -> {
          while (change.next()) {
            filterCollection.setFilter("authorName", change.getList().stream().map(
                Author::getAuthorName).toList());
          }
          parent.refreshDataViews();
        });
  }

  private List<Author> prepareAuthorFilters(){
    return databaseManager.getAuthors().stream().sorted(Comparator.comparing(Author::getAuthorName)).toList();
  }

  public void reloadFilters() {
    authorsFilter.getItems().clear();
    authorsFilter.getItems().addAll(prepareAuthorFilters());
  }

  @Override
  public void setParent(Controller parent) {
    this.parent =(LibraryViewController) parent;
  }
}
