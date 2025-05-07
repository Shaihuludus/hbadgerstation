package io.maddsoft.hbadgerstation.gui.gridview;

import impl.org.controlsfx.skin.GridViewSkin;
import io.maddsoft.hbadgerstation.gui.GUIDefaults;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.GridView;

public class CustomGridViewSkin<T> extends GridViewSkin<T> {

  @Getter
  @Setter
  private int maxItemsInRow = GUIDefaults.PRINTABLE_GRID_COLUMNS;

  public CustomGridViewSkin(GridView<T> control) {
    super(control);
  }

  @Override
  public int computeMaxCellsInRow() {
    return Math.min(super.computeMaxCellsInRow(), maxItemsInRow);
  }
}
