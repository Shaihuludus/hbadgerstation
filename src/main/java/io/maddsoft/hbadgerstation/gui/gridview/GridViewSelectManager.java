package io.maddsoft.hbadgerstation.gui.gridview;

import io.maddsoft.hbadgerstation.gui.GUIDefaults;
import io.maddsoft.hbadgerstation.gui.printableview.PrintableViewController;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class GridViewSelectManager {

  private final List<GridCellSelectionController> controllerToNotify = new ArrayList<>();

  @Getter
  private GridCellController currentSelectedView = null;

  public boolean isPrintableViewSelected() {
    return currentSelectedView != null;
  }

  public void addGridControllerToNotify(GridCellSelectionController controller){
    this.controllerToNotify.add(controller);
  }

  public void setPrintableViewSelected(PrintableViewController newSelectedView) {
   if (currentSelectedView != newSelectedView) {
     if (currentSelectedView != null) {
       currentSelectedView.setBackgroundStyle("");
     }
     currentSelectedView = newSelectedView;
     currentSelectedView.setBackgroundStyle(GUIDefaults.DRACULA_SELECTED_BACKGROUND);
     notifyControllers();
   }
  }

  public void runControllersAction() {
    controllerToNotify.forEach(GridCellSelectionController::performAction);
  }

  private void notifyControllers() {
    controllerToNotify.forEach(controller -> {
      controller.selectedCell(currentSelectedView);
    } );
  }


}
