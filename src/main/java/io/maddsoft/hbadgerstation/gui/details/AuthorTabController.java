package io.maddsoft.hbadgerstation.gui.details;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellController;
import io.maddsoft.hbadgerstation.gui.gridview.GridCellSelectionController;
import io.maddsoft.hbadgerstation.storage.entities.Author;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.controlsfx.control.HyperlinkLabel;

public class AuthorTabController implements GridCellSelectionController {

  private PrintableDetailsViewController detailsViewController;

  private Author author;

  @FXML private TextField authorNameField;
  @FXML private HyperlinkLabel authorWebsiteField;

  public void initialize(Author author){
    this.author = author;
    authorNameField.setText(author.getAuthorName());
    authorWebsiteField.setText("[" + author.getWebsiteUrl() + "]");
  }

  @Override
  public void selectedCell(GridCellController controller) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void performAction() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void scrollToLastCell() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void selectLastCell() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setParent(Controller parent) {
    detailsViewController = (PrintableDetailsViewController) parent;
  }
}
