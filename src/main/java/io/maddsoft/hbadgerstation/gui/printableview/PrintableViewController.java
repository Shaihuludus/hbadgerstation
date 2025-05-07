package io.maddsoft.hbadgerstation.gui.printableview;

import io.maddsoft.hbadgerstation.gui.GUIDefaults;
import io.maddsoft.hbadgerstation.gui.elements.PrintableThingTableElement;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellController;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewSelectManager;
import java.net.MalformedURLException;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.GridCell;

public class PrintableViewController extends GridCell<PrintableThingTableElement> implements
    GridCellController {

  @FXML private ImageView imageView;
  @FXML private Label printableName;
  @FXML private Label authorName;
  @FXML private Label type;
  @FXML private VBox printableViewRoot;

  private GridViewSelectManager gridViewSelectManager;

  @Getter
  private PrintableThingTableElement printableThingTableElement;

  public void initialize(PrintableThingTableElement printableThingTableElement, GridViewSelectManager gridViewSelectManager) throws MalformedURLException {
    this.printableThingTableElement = printableThingTableElement;
    this.gridViewSelectManager = gridViewSelectManager;
    imageView.setImage(
        StringUtils.isNotBlank(printableThingTableElement.getPreviewImage()) ?
            new Image(printableThingTableElement.getPreviewImage(), true) :
            new Image(Objects.requireNonNull(getClass().getResourceAsStream(GUIDefaults.DEFAULT_IMAGE))));
    imageView.setFitWidth(GUIDefaults.PRINTABLE_IMAGE_MINIATURE_WIDTH);
    imageView.setPreserveRatio(true);
    printableName.setText(printableThingTableElement.getName());
    authorName.setText(printableThingTableElement.getAuthorName());
    type.setText(printableThingTableElement.getType());
  }

  public void onMouseClicked(MouseEvent mouseEvent) {
    gridViewSelectManager.setPrintableViewSelected(this);
  }

  public void setBackgroundStyle(String style) {
    printableViewRoot.setStyle(style);
  }
}
