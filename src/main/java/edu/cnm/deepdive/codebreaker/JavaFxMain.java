package edu.cnm.deepdive.codebreaker;

import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxMain extends Application {

  private static final String BUNDLE_BASE_NAME = "game";
  private static final String MAIN_LAYOUT = "layouts/main.fxml";
  private static final String WINDOW_TITLE = "window_title";

  static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME);

    ClassLoader classLoader = getClass().getClassLoader();

    FXMLLoader fxmlLoader = new FXMLLoader(
        classLoader.getResource(MAIN_LAYOUT),
        bundle
    );

    Scene scene = new Scene(fxmlLoader.load());

    stage.setTitle(bundle.getString(WINDOW_TITLE));
    stage.setScene(scene);
    stage.show();
  }
}
