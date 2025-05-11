package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.gui.GUIDefaults;
import io.maddsoft.hbadgerstation.gui.elements.PrintableThingTableElement.PrintableThingTableElementConverter;
import io.maddsoft.hbadgerstation.gui.gridview.CustomGridViewSkin;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellController;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellSelectionController;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewGridCellCallback;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewSelectManager;
import io.maddsoft.hbadgerstation.gui.printableview.PrintableViewController;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.FilterCollection;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

  @FXML private TextField authorNameField;
  @FXML private HyperlinkLabel authorWebsiteField;
  @FXML private GridView<VBox> modelsGrid;

  private NitriteId printableId;

  public void initialize(Author author, NitriteId printableId) {
    this.author = author;
    this.printableId = printableId;
    authorNameField.setText(author.getAuthorName());
    authorWebsiteField.setText("[" + author.getWebsiteUrl() + "]");
    modelsGrid.setItems(FXCollections.observableArrayList(prepareAuthorModels()));
    modelsGrid.setCellHeight(400);
    modelsGrid.setCellWidth(400);
    modelsGrid.setSkin(new CustomGridViewSkin<>(modelsGrid));
    modelsGrid.setCellFactory(new GridViewGridCellCallback());
  }

  private List<VBox> prepareAuthorModels() {
    FilterCollection filterCollection = new FilterCollection();
    filterCollection.setFilter("authorName", Collections.singletonList(author.getAuthorName()));
    AtomicInteger tempPosition = new AtomicInteger(0);
    return databaseManager.getPrintableThings(filterCollection).stream()
        .filter(printableThing -> !printableThing.getPrintableThingId().equals(printableId))
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
}
