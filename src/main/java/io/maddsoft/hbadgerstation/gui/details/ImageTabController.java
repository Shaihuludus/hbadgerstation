package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.GUIDefaults;
import io.maddsoft.hbadgerstation.gui.ImageViewController;
import io.maddsoft.hbadgerstation.gui.gridview.CustomGridViewSkin;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellController;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellSelectionController;
import io.maddsoft.hbadgerstation.gui.gridview.GridViewGridCellCallback;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.GridView;

@Slf4j
public class ImageTabController implements GridCellSelectionController {

  @FXML private GridView<VBox> imagesGrid;


  public void initialize(List<String> images) {
    imagesGrid.setItems(FXCollections.observableArrayList(prepareImagesView(images)));
    imagesGrid.setCellWidth(GUIDefaults.IMAGE_CELL_SIZE);
    imagesGrid.setCellHeight(GUIDefaults.IMAGE_CELL_SIZE);
    imagesGrid.setSkin(new CustomGridViewSkin<>(imagesGrid));
    imagesGrid.setCellFactory(new GridViewGridCellCallback());
  }

  private List<VBox> prepareImagesView(List<String> images) {
    return images.stream().map(image -> {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/imageView.fxml"));
      try {
        VBox imageViewRoot = fxmlLoader.load();
        ImageViewController imageViewController = fxmlLoader.getController();
            imageViewController.initialize(image);
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
    throw new UnsupportedOperationException();
  }

  @Override
  public void performAction() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void scrollToLastCell() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void selectLastCell() {
    throw new UnsupportedOperationException();
  }
}
