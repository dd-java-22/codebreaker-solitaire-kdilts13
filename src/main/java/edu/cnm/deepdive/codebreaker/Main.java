package edu.cnm.deepdive.codebreaker;

import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;

public class Main {

  static void main() {
    GameViewModel viewModel = GameViewModel.getInstance();

    viewModel.registerGameObserver(System.out::println);

    viewModel.startGame("ABCDEF", 2);

    System.out.println("game start requested");
  }
}
