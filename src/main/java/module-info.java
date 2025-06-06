module hbadgerstation {
  requires imgscalr.lib;
  requires java.desktop;
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires static lombok;
  requires org.apache.commons.configuration2;
  requires org.apache.commons.io;
  requires org.apache.commons.lang3;
  requires org.controlsfx.controls;
  requires org.dizitart.no2;
  requires org.dizitart.no2.mvstore;
  requires org.slf4j;
  requires org.apache.logging.log4j;

  exports io.maddsoft.hbadgerstation;

  opens io.maddsoft.hbadgerstation.gui;
  opens io.maddsoft.hbadgerstation.gui.library;
  opens io.maddsoft.hbadgerstation.gui.printableview;
  opens io.maddsoft.hbadgerstation.gui.details;
  opens io.maddsoft.hbadgerstation.gui.preview;
  opens io.maddsoft.hbadgerstation.gui.controls;
}