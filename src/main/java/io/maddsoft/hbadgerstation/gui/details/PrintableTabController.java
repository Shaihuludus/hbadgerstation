package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

@Slf4j
public class PrintableTabController implements Controller {

  @FXML private TreeView<PrintableTextPath> printableTree;
  @FXML private Button openButton;

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
    printableTree.getSelectionModel().selectedItemProperty().addListener((_, oldValue, newValue) -> {
      activate();
    });
  }

  private void activate() {
    openButton.setDisable(false);
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
}
