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

  @Override
  protected void layoutChildren(double x, double y, double w, double h) {
    double insetLeft = this.getSkinnable().getInsets().getLeft();
    double insetRight = this.getSkinnable().getInsets().getRight();
    double gridWith = this.getSkinnable().getWidth();
    double x1 = Math.max(insetLeft , (gridWith - countRowWidth())/2);
    double y1 = this.getSkinnable().getInsets().getTop();
    double w1 = gridWith - x1 + insetRight;
    double h1 = this.getSkinnable().getHeight() - this.getSkinnable().getInsets().getTop() + this.getSkinnable().getInsets().getBottom();
    this.getVirtualFlow().resizeRelocate(x1, y1, w1, h1);
  }

  private double countRowWidth() {
    int countMaxCellsInRow = Math.min(this.getSkinnable().getItems().size(), computeMaxCellsInRow());
    double cellWidth = getSkinnable().getCellWidth();
    return countMaxCellsInRow * cellWidth + (countMaxCellsInRow-1) * getSkinnable().getHorizontalCellSpacing();
  }

}
