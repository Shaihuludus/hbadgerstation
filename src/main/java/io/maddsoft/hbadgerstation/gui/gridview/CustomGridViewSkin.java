package io.maddsoft.hbadgerstation.gui.gridview;

import impl.org.controlsfx.skin.GridViewSkin;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.GridView;

public class CustomGridViewSkin<T> extends GridViewSkin<T> {

  @Getter
  @Setter
  private int maxItemsInRow = 0;

  public CustomGridViewSkin(GridView<T> control) {
    super(control);
  }

  @Override
  public int computeMaxCellsInRow() {
    return maxItemsInRow <=0 ? super.computeMaxCellsInRow() : Math.min(super.computeMaxCellsInRow(), maxItemsInRow);
  }
}
