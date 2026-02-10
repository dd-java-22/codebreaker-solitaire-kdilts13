package edu.cnm.deepdive.codebreaker;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

  private final BlockingQueue<Game> updateQueue = new LinkedBlockingQueue<>();
  private boolean solved;

  void main() {
    GameViewModel viewModel = GameViewModel.getInstance();

    viewModel.registerGameObserver((updateQueue::add));

    viewModel.registerGuessObserver(System.out::println);
    viewModel.registerErrorObserver(System.out::println);

    viewModel.startGame("ABCDEF", 2);

    while (!solved) {
      try {
        Game game = updateQueue.take();

        solved = Boolean.TRUE.equals(game.getSolved());

        System.out.println("-------------------------------");
        System.out.println("Guesses:");
        //noinspection DataFlowIssue
        game.getGuesses().forEach(System.out::println);
        System.out.println();

        System.out.println("Game:");
        System.out.println(game);
        System.out.println();
        System.out.println("-------------------------------");

        if (solved) {
          System.out.println("You solved it!");
        } else {
          // TODO: 2/10/2026 get next guess from user
          // TODO: 2/10/2026 submit new guess
        }

      } catch (InterruptedException e) {
        //noinspection CallToPrintStackTrace
        e.printStackTrace();
      }
    }
  }
}
