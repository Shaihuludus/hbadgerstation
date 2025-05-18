package io.maddsoft.hbadgerstation.gui.gridview;

import io.maddsoft.hbadgerstation.gui.GUIDefaults;
import io.maddsoft.hbadgerstation.gui.support.PrintableThingTableElement.PrintableThingTableElementConverter;
import io.maddsoft.hbadgerstation.gui.printableview.PrintableViewController;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.FilterCollection;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GridViewPrintableBuilder {

  public List<VBox> buildPrintable(FilterCollection filterCollection, GridViewSelectManager gridViewSelectManager, boolean showAuthorName) {
    AtomicInteger tempPosition = new AtomicInteger(0);
    return new DatabaseManager().getPrintableThings(filterCollection).stream()
        .map(PrintableThingTableElementConverter::convert).map(printableThingTableElement -> {
          FXMLLoader fxmlLoader = new FXMLLoader();
          fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/printableView.fxml"));
          try {
            VBox imageViewRoot = fxmlLoader.load();
            printableThingTableElement.setListPosition(tempPosition.getAndIncrement());
            PrintableViewController imageViewController = fxmlLoader.getController();
            imageViewRoot.setOnMouseClicked(imageViewController::onMouseClicked);
            imageViewController.initialize(printableThingTableElement, gridViewSelectManager, showAuthorName);
            imageViewRoot.prefWidth(GUIDefaults.IMAGE_DISPLAY_SIZE);
            return imageViewRoot;
          } catch (IOException e) {
            log.error(e.getMessage());
          }
          return null;
        }).toList();
  }

}
