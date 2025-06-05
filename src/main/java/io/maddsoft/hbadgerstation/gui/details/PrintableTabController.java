package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.gui.preview.PreviewWindowController;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

@Slf4j
public class PrintableTabController implements Controller {

  @FXML private TreeView<PrintableTextPath> printableTree;
  @FXML private Button openButton;
  @FXML private Button showSTLButton;

  private Stage previewWindowStage;
  private PreviewWindowController previewWindowController;

  private String directoryPath;

  public void initialize(List<String> printableFiles, String directoryPath) {
    this.directoryPath = StringUtils.stripEnd(directoryPath, "/");
    createTree(printableFiles);
  }

  private void createTree(List<String> printableFiles){
    String root = StringUtils.substringAfterLast(directoryPath, "/");
    printableTree.setRoot(new TreeItem<>(new PrintableTextPath(root, directoryPath), new Glyph("FontAwesome", FontAwesome.Glyph.FOLDER)));
    printableFiles.forEach(this::createTreePath);
    printableTree.getRoot().setExpanded(true);
    printableTree.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> activate());
  }

  private void activate() {
    openButton.setDisable(false);
    showSTLButton.setDisable(false);
    TreeItem<PrintableTextPath> selectedItem = printableTree.getSelectionModel()
        .getSelectedItem();
    if(selectedItem != null) {
      String path = selectedItem.getValue().getPath();
      showSTLButton.setDisable(!StringUtils.substringAfterLast(path, ".").equalsIgnoreCase("stl"));
    }
  }

  private void createTreePath(String printableFilePath) {
    String[] nodes = StringUtils.stripStart(StringUtils.substringAfter(printableFilePath.replace("\\", "/"), directoryPath), "/").split("/");
    StringBuilder currentPath = new StringBuilder(directoryPath);
    AtomicReference<TreeItem<PrintableTextPath>> checkedTreeItem = new AtomicReference<>(printableTree.getRoot());
    AtomicBoolean found = new AtomicBoolean(false);
    AtomicBoolean skipValidating = new AtomicBoolean(false);
    for (String node : nodes) {
      currentPath.append("/").append(node);
      if(!skipValidating.get()) {
        checkedTreeItem.get().getChildren()
            .stream()
            .filter(treeItem -> treeItem.getValue().getText().equals(node))
            .findFirst()
            .ifPresent(treeItem -> {
              checkedTreeItem.set(treeItem);
              found.set(true);
            });
        if (found.get()) {
          found.set(false);
          continue;
        } else {
          skipValidating.set(true);
        }
      }
      TreeItem<PrintableTextPath> newItem = node.contains(".") ? new TreeItem<>(
          new PrintableTextPath(node, currentPath.toString())) : new TreeItem<>(new PrintableTextPath(node, currentPath.toString()), new Glyph("FontAwesome", FontAwesome.Glyph.FOLDER));
      checkedTreeItem.get().getChildren().add(newItem);
      checkedTreeItem.set(newItem);
    }
  }

  public void openPrintableFile() {
    try {
      TreeItem<PrintableTextPath> selectedItem = printableTree.getSelectionModel()
          .getSelectedItem();
      if(selectedItem != null) {
        Desktop.getDesktop().open(new File(selectedItem.getValue().getPath()));
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public void renderStlFile() {
    TreeItem<PrintableTextPath> selectedItem = printableTree.getSelectionModel()
        .getSelectedItem();
    if ( previewWindowStage == null || previewWindowController == null ) {
      createPreviewStage();
    }
    previewWindowStage.show();
    previewWindowController.initialize(selectedItem.getValue().getPath());
  }

  private void createPreviewStage() {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/previewwindow.fxml"));
      Parent view = fxmlLoader.load();
      previewWindowController = fxmlLoader.getController();
      previewWindowController.setParent(this);
      Scene scene = new Scene(view);
      previewWindowStage = new Stage();
      previewWindowStage.setResizable(true);
      previewWindowStage.setScene(scene);
    } catch (IOException e) {
      log.error("Failed to create new Window.", e);
    }
  }
}
