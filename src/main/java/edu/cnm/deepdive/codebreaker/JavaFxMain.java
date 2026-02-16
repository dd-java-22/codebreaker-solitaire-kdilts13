package edu.cnm.deepdive.codebreaker;

import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxMain extends Application {

  static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    ResourceBundle bundle = ResourceBundle.getBundle("strings");

    ClassLoader classLoader = getClass().getClassLoader();

    FXMLLoader fxmlLoader = new FXMLLoader(
        classLoader.getResource("layouts/main.fxml"),
        bundle
    );

    Scene scene = new Scene(fxmlLoader.load());

    stage.setTitle(bundle.getString("window_title"));
    stage.setScene(scene);
    stage.show();
  }
}
