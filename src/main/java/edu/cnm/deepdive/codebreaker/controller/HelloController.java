package edu.cnm.deepdive.codebreaker.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class HelloController {

  private  static final String GREETING_TEXT = "Hello, brave new world!";

  @FXML
  private Text greeting;

  @FXML
  private void initialize() {
    greeting.setText(GREETING_TEXT);
  }

}
