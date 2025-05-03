package io.maddsoft.hbadgerstation.gui;

public interface Controller {

  default void refreshDataViews() {
  }

  default void setParent(Controller parent) {
  }
}
