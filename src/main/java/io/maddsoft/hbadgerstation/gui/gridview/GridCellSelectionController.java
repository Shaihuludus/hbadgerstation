package io.maddsoft.hbadgerstation.gui.gridview;

import io.maddsoft.hbadgerstation.gui.Controller;

public interface GridCellSelectionController extends Controller {

  void selectedCell(GridCellController controller);

  void performAction();

  void scrollToLastCell();

  void selectLastCell();

  default int calculateItemIndex(CustomGridViewSkin<?> skin, int itemPosition) {
    return itemPosition / skin.computeMaxCellsInRow();
  }
}
