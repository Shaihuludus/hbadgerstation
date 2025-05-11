package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.gui.gridview.CustomGridViewSkin;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellController;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellSelectionController;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewGridCellCallback;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewPrintableBuilder;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewSelectManager;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.FilterCollection;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.GridView;
import org.controlsfx.control.HyperlinkLabel;
import org.dizitart.no2.collection.NitriteId;

@Slf4j
public class AuthorTabController implements GridCellSelectionController {

  private PrintableDetailsViewController detailsViewController;

  private Author author;

  private DatabaseManager databaseManager = new DatabaseManager();

  private final GridViewSelectManager gridViewSelectManager = new GridViewSelectManager();

  @FXML private ComboBox<Author> authorChooseBox;
  @FXML private TextField authorNameField;
  @FXML private HyperlinkLabel authorWebsiteField;
  @FXML private GridView<VBox> modelsGrid;

  private NitriteId printableId;

  public void initialize(Author author, NitriteId printableId) {
    this.author = author;
    this.printableId = printableId;
    authorNameField.setText(author.getAuthorName());
    authorNameField.textProperty().addListener((_, _, newValue) -> {
      detailsViewController.changeHappened();
    });
    authorWebsiteField.setText("[" + author.getWebsiteUrl() + "]");
    modelsGrid.setItems(FXCollections.observableArrayList(prepareAuthorModels()));
    modelsGrid.setCellHeight(400);
    modelsGrid.setCellWidth(400);
    modelsGrid.setSkin(new CustomGridViewSkin<>(modelsGrid));
    modelsGrid.setCellFactory(new GridViewGridCellCallback());
    setupAutorSelector();
  }

  private void setupAutorSelector() {
    authorChooseBox.setVisible(false);
    authorChooseBox.getItems().addAll(prepareAuthor());
    authorChooseBox.getSelectionModel().select(this.author);
    authorChooseBox.setManaged(false);
    authorChooseBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
      authorNameField.setText(newValue.toString());
      authorWebsiteField.setText("[" + newValue.getWebsiteUrl() + "]");
      detailsViewController.changeHappened();
    });
  }

  private List<Author> prepareAuthor(){
    return databaseManager.getAuthors().stream().sorted(Comparator.comparing(Author::getAuthorName)).toList();
  }

  private List<VBox> prepareAuthorModels() {
    FilterCollection filterCollection = new FilterCollection();
    filterCollection.setFilter("authorName", Collections.singletonList(author.getAuthorName()));
    filterCollection.setNotIdFilter("printableThingId", Collections.singletonList(printableId));
    return new GridViewPrintableBuilder().buildPrintable(filterCollection, gridViewSelectManager, false);
  }

  public void setEditable(boolean lock){
    authorChooseBox.setVisible(lock);
    authorChooseBox.setManaged(lock);
    authorNameField.setEditable(lock);
  }

  @Override
  public void selectedCell(GridCellController controller) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void performAction() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void scrollToLastCell() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void selectLastCell() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setParent(Controller parent) {
    detailsViewController = (PrintableDetailsViewController) parent;
  }

  public Author updateAuthor() {
    if(isNewAuthor()) {
      author.setWebsiteUrl(authorWebsiteField.getText());
    }
    author.setAuthorName(authorNameField.getText());
    return author;
  }

  public boolean isNewAuthor() {
    return authorChooseBox.getItems().stream().noneMatch(item -> item.getAuthorName().equals(authorNameField.getText()));
  }
}
