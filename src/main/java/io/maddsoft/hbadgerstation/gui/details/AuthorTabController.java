package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.gui.gridview.CustomGridViewSkin;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewGridCellCallback;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewPrintableBuilder;
import io.maddsoft.hbadgerstation.gui.gridview.PrintableThingGridSelectionController;
import io.maddsoft.hbadgerstation.storage.FilterCollection;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.GridView;
import org.controlsfx.control.HyperlinkLabel;
import org.dizitart.no2.collection.NitriteId;

@Slf4j
public class AuthorTabController extends PrintableThingGridSelectionController {

  private PrintableDetailsViewController detailsViewController;

  private Author author;

  @FXML private ComboBox<Author> authorChooseBox;
  @FXML private TextField authorNameField;
  @FXML private HyperlinkLabel authorWebsiteField;
  @FXML private GridView<VBox> modelsGrid;

  private NitriteId printableId;

  /** Raw website URL of the currently shown author (the label text is decorated with [] for HyperlinkLabel). */
  private String websiteUrl;

  public void initialize(Author author, NitriteId printableId) {
    gridViewSelectManager.addGridControllerToNotify(this);
    this.author = author;
    this.printableId = printableId;
    authorNameField.setText(author.getAuthorName());
    authorNameField.textProperty().addListener((_, _, _) -> detailsViewController.changeHappened());
    showWebsite(author.getWebsiteUrl());
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
      if (newValue == null) {
        return;
      }
      authorNameField.setText(newValue.getAuthorName());
      showWebsite(newValue.getWebsiteUrl());
      detailsViewController.changeHappened();
    });
  }

  /** HyperlinkLabel renders text in [] as a link. */
  private void showWebsite(String url) {
    websiteUrl = url;
    authorWebsiteField.setText(StringUtils.isBlank(url) ? "" : "[" + url + "]");
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
  public void performAction() {
    detailsViewController.switchSelectedPrintableThing(selectedItem);
  }

  @Override
  public void setParent(Controller parent) {
    detailsViewController = (PrintableDetailsViewController) parent;
  }

  /**
   * Returns the author the user switched to, or empty when the author was not changed (same name
   * as shown initially, or a blank name). The result is the existing author with a matching name,
   * or a new, not yet persisted author carrying the currently shown website URL (may be empty).
   */
  public Optional<Author> getChangedAuthor() {
    String name = StringUtils.trimToEmpty(authorNameField.getText());
    if (name.isEmpty() || name.equals(author.getAuthorName())) {
      return Optional.empty();
    }
    return Optional.of(authorChooseBox.getItems().stream()
        .filter(item -> name.equals(item.getAuthorName()))
        .findFirst()
        .orElseGet(() -> Author.builder().authorName(name).websiteUrl(StringUtils.defaultString(websiteUrl)).build()));
  }
}
