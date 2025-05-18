package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.gui.controls.PillsController;
import io.maddsoft.hbadgerstation.gui.controls.RemovablePill;
import io.maddsoft.hbadgerstation.storage.DatabaseManager;
import io.maddsoft.hbadgerstation.storage.entities.PrintableThing;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class GeneralTabController implements Controller, PillsController {

  @FXML private HBox newTagsGroup;
  @FXML private TextField newTagsField;
  @FXML private Button addTagsButton;
  @FXML private FlowPane tagsBox;
  @FXML private TextField typeField;
  @FXML private ComboBox<String> typeSelectBox;
  @FXML private TextField nameField;
  @FXML private TextArea descriptionField;
  @FXML private TextField directoryPathField;

  private PrintableDetailsViewController parent;

  private List<String> tagsCache = new ArrayList<>();

  private PrintableThing printableThing;

  private final DatabaseManager databaseManager = new DatabaseManager();

  public void initialize(PrintableThing printableThing) {
    this.printableThing = printableThing;
    typeSelectBox.getItems().addAll(PrintableThing.PRINTABLE_THING_TYPES);
    if (this.printableThing != null) {
      nameField.setText(this.printableThing.getName());
      nameField.textProperty().addListener((_, _, newValue) -> {

        if (!Objects.equals(this.printableThing.getName(), newValue)){
          parent.changeHappened();
        }
      });
      descriptionField.setText(this.printableThing.getDescription());
      descriptionField.textProperty().addListener((_, _, newValue) -> {
        if (!Objects.equals(this.printableThing.getDescription(), newValue)){
          parent.changeHappened();
        }
      });
      directoryPathField.setText(this.printableThing.getDirectoryPath());
      typeField.setText(this.printableThing.getType());
      if (StringUtils.isNotBlank(this.printableThing.getType())) {
        typeSelectBox.getSelectionModel().select(this.printableThing.getType());
      }
      typeSelectBox.valueProperty().addListener((_, _, newValue) -> {
        if (!Objects.equals(this.printableThing.getType(), newValue)){
          typeField.setText(newValue);
          parent.changeHappened();
        }
      });
      initializeTags();
    }
  }

  @Override
  public void setParent(Controller parent) {
    this.parent = (PrintableDetailsViewController) parent;
  }

  public void setEditable(boolean lock) {
    nameField.setEditable(lock);
    descriptionField.setEditable(lock);
    directoryPathField.setEditable(lock);
    if (lock) {
      typeSelectBox.setDisable(false);
      typeSelectBox.setVisible(true);
      typeField.setVisible(false);
      typeField.setManaged(false);
      newTagsGroup.setVisible(true);
      newTagsGroup.setManaged(true);
      tagsBox.getChildren().forEach(child -> {
        RemovablePill removablePillController = (RemovablePill) child.getUserData();
        removablePillController.disableRemoveButton(false);
      });
    } else {
      typeSelectBox.setDisable(true);
      typeSelectBox.setVisible(false);
      typeField.setVisible(true);
      typeField.setManaged(true);
      newTagsGroup.setVisible(false);
      newTagsGroup.setManaged(false);
      tagsBox.getChildren().forEach(child -> {
        RemovablePill removablePillController = (RemovablePill) child.getUserData();
        removablePillController.disableRemoveButton(true);
      });
    }
  }

  public void updatePrintableThing() {
    printableThing.setName(nameField.getText());
    printableThing.setDescription(descriptionField.getText());
    printableThing.setDirectoryPath(directoryPathField.getText());
    printableThing.setType(typeField.getText());
    printableThing.setTags(new ArrayList<>(tagsCache));
  }

  @Override
  public void pillButtonPressed(String pillName) {
    tagsCache.stream()
        .filter(tag -> tag.equals(pillName))
        .findFirst()
        .ifPresent(tag -> tagsCache.remove(tag));
    tagsBox.getChildren()
        .stream()
        .filter(child -> ((RemovablePill) child.getUserData()).getPillName().equals(pillName))
        .findFirst()
        .ifPresent(child -> tagsBox.getChildren().remove(child));
    parent.changeHappened();
  }

  private void initializeTags() {
    tagsBox.getChildren().clear();
    newTagsGroup.setManaged(false);
    addTagsButton.setOnAction(_ -> addNewTags());
    if (printableThing.getTags() != null) {
      tagsCache = new ArrayList<>(printableThing.getTags());
    }
    tagsCache.forEach(tag -> {
      try {
        tagsBox.getChildren().add(createTag(tag));
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    });
  }

  private void addNewTags() {
    String newTags = newTagsField.getText();
    if (StringUtils.isNotBlank(newTags)) {
       String[] tags = newTags.split(",");
       for (String tag : tags) {
         if (tagsCache.stream().noneMatch(existingTag -> existingTag.equals(tag))) {
           tagsCache.add(tag);
           parent.changeHappened();
           databaseManager.addTag(tag);
           try {
             tagsBox.getChildren().add(createTag(tag));
           } catch (IOException e) {
             log.error(e.getMessage(), e);
           }
         }
       }
       newTagsField.clear();
    }
  }

  private Node createTag(String name) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/io/maddsoft/hbadgerstation/controls/removablePill.fxml"));
    Node tag = fxmlLoader.load();
    RemovablePill controller =fxmlLoader.getController();
    controller.setParent(this);
    controller.initialize(name);
    return tag;
  }
}
