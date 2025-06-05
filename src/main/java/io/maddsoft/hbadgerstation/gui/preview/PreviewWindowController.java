package io.maddsoft.hbadgerstation.gui.preview;

import io.maddsoft.hbadgerstation.gui.Controller;
import io.maddsoft.hbadgerstation.stl.StlLoader;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PreviewWindowController implements Controller {

  @FXML
  private StackPane subSceneContainer;

  private PerspectiveCamera camera;
  private final StlLoader stlLoader = new StlLoader();
  private MeshView meshView;

  private double scaleFactor = 1.0;

  public void initialize(String path) {
    try {
      subSceneContainer.getChildren().clear();
      System.gc();
      buildScene(path);
      initCamera();
      Group group = new Group();
      SubScene scene3D = createScene3D(group);
      group.getChildren().add(meshView);
      subSceneContainer.getChildren().add(scene3D);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public void zoomIn() {
    scaleFactor++;
    scaleMeshView();
  }

  private void scaleMeshView() {
    meshView.setScaleX(scaleFactor);
    meshView.setScaleY(scaleFactor);
    meshView.setScaleZ(scaleFactor);
  }

  public void zoomOut() {
    scaleFactor--;
    meshView.setScaleX(scaleFactor);
    meshView.setScaleY(scaleFactor);
    meshView.setScaleZ(scaleFactor);
  }

  private void initCamera() {
    this.camera = new PerspectiveCamera(true);
    this.camera.setNearClip(0.1);
    this.camera.setFarClip(10000.0);
    this.camera.setTranslateZ(-2000);
  }

  private void buildScene(String printableFilePath) throws IOException {
    TriangleMesh mesh = stlLoader.loadStl(printableFilePath);
    meshView = new MeshView(mesh);
    double meshWidth = meshView.getLayoutBounds().getWidth();
    double meshHeight = meshView.getLayoutBounds().getHeight();
    double scaleWidth = subSceneContainer.getWidth() / meshWidth;
    double scaleHeight = subSceneContainer.getHeight() / meshHeight;
    scaleFactor = Math.min(scaleWidth, scaleHeight) / 3;
    fixMeshView();
    scaleMeshView();
  }

  private SubScene createScene3D(Group group) {
    SubScene scene3d = new SubScene(group, subSceneContainer.getWidth(),
        subSceneContainer.getHeight(), true, SceneAntialiasing.BALANCED);
    scene3d.widthProperty().bind(subSceneContainer.widthProperty());
    scene3d.heightProperty().bind(subSceneContainer.heightProperty());
    scene3d.setFill(Color.GRAY);
    scene3d.setCamera(camera);
    scene3d.setPickOnBounds(true);
    return scene3d;
  }

  private void fixMeshView() {
    meshView.setTranslateY(-meshView.getLayoutBounds().getMinY());
    meshView.setTranslateX(-meshView.getLayoutBounds().getMinX());
  }
}
