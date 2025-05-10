package io.maddsoft.hbadgerstation.gui.controls;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.HyperlinkLabel;

public class ExtendedHyperLinkLabel extends HyperlinkLabel {

  @Getter
  private final String fullLinkText;

  public ExtendedHyperLinkLabel(String text, String fullLinkText) {
    super(text);
    this.fullLinkText = fullLinkText;
  }

  public String getFormattedText() {
    return StringUtils.stripEnd(StringUtils.stripStart(super.getText(), "["), "]");
  }
}
