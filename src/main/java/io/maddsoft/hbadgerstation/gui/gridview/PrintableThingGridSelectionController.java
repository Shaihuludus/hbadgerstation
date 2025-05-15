package io.maddsoft.hbadgerstation.gui.gridview;

import io.maddsoft.hbadgerstation.gui.elements.PrintableThingTableElement;
import io.maddsoft.hbadgerstation.gui.printableview.PrintableViewController;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import lombok.Getter;
import lombok.Setter;

public abstract class PrintableThingGridSelectionController implements GridCellSelectionController{

  @Setter
  @Getter
  protected PrintableThingTableElement selectedItem;

  protected final GridViewSelectManager gridViewSelectManager = new GridViewSelectManager();

  protected final DatabaseManager databaseManager = new DatabaseManager();

  @Override
  public void selectedCell(GridCellController controller) {
    if (controller == null) {
      selectedItem = null;
    }  else {
      selectedItem = ((PrintableViewController) controller).getPrintableThingTableElement();
    }
  }

}
