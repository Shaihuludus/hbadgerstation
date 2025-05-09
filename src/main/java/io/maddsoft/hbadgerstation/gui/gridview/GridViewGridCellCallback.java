package io.maddsoft.hbadgerstation.gui.gridview;

import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

public class GridViewGridCellCallback implements
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

