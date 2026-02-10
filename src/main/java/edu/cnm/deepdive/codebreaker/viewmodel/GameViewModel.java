package edu.cnm.deepdive.codebreaker.viewmodel;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import edu.cnm.deepdive.codebreaker.service.CodebreakerService;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class GameViewModel {

  private final CodebreakerService service;
  private final List<Consumer<Game>> gameObservers;
  private final List<Consumer<Guess>> guessObservers;
  private final List<Consumer<Throwable>> errorObservers;

  private Game game;
  private Guess guess;
  private Throwable error;

  static GameViewModel getInstance() {
    return Holder.INSTANCE;
  }

  private GameViewModel() {
    service = CodebreakerService.getInstance();

    gameObservers = new LinkedList<>();
    guessObservers = new LinkedList<>();
    errorObservers = new LinkedList<>();
  }

  public void setGame(Game game) {
    this.game = game;

    gameObservers.forEach((Consumer<Game> consumer) -> {
      consumer.accept(game);
    });
  }

  public void setGuess(Guess guess) {
    this.guess = guess;

    guessObservers.forEach((Consumer<Guess> consumer) -> {
      consumer.accept(guess);
    });
  }

  public void setError(Throwable error) {
    this.error = error;

    errorObservers.forEach((Consumer<Throwable> consumer) -> {
      consumer.accept(error);
    });
  }

  // TODO: 2/10/2026 Add game methods

  private static class Holder {
    static final GameViewModel INSTANCE = new GameViewModel();
  }
}
