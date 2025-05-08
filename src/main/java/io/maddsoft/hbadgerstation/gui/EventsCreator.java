package io.maddsoft.hbadgerstation.gui;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class EventsCreator {

  public static void fireMouseClicked(Node node) {
    Event.fireEvent(node
        ,new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
            0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
            true, true, true, true, true, true, null));
  }

  private EventsCreator() {}
}
