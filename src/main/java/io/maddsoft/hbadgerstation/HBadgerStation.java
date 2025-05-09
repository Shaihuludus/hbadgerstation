package io.maddsoft.hbadgerstation;

import io.maddsoft.hbadgerstation.gui.MainWindowController;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HBadgerStation extends Application {

  private static Scene scene;

  @Override
  public void start(Stage stage) throws IOException {
    Application.setUserAgentStylesheet("dracula.css");
    FXMLLoader fxmlLoader = new FXMLLoader(HBadgerStation.class.getResource( "mainwindow.fxml"));
    Parent loaded = fxmlLoader.load();
    scene = new Scene(loaded);
    stage.setScene(scene);
    stage.setMaximized(true);
    stage.setOnCloseRequest(e -> Platform.exit());
    stage.show();
    ((MainWindowController) fxmlLoader.getController()).postConstruct();
  }

  static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFXML(fxml));
  }

  private static Parent loadFXML(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(HBadgerStation.class.getResource(fxml + ".fxml"));
    return fxmlLoader.load();
  }

  public static void main(String[] args) {
    launch();
  }

}
