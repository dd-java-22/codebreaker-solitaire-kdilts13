package edu.cnm.deepdive.codebreaker.viewmodel;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import edu.cnm.deepdive.codebreaker.service.CodebreakerService;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Platform;

@SuppressWarnings({"UnusedReturnValue", "CallToPrintStackTrace", "unused"})
/**
 * View model coordinating game and guess operations with {@link CodebreakerService} and notifying
 * registered observers of state changes.
 *
 * <p>This type is implemented as a singleton accessible via {@link #getInstance()}.
 *
 * <p>Observers are invoked on the JavaFX Application Thread via {@link Platform#runLater(Runnable)}.
 * When an observer is registered and a corresponding value is already available, the observer is
 * invoked immediately with the most recently known value.
 *
 * <p>Service calls are asynchronous; methods that initiate service work return immediately, and
 * observers are notified when results arrive.
 */
public class GameViewModel {

  private final CodebreakerService service;
  private final List<Consumer<Game>> gameObservers;
  private final List<Consumer<Guess>> guessObservers;
  private final List<Consumer<Throwable>> errorObservers;
  private final List<Consumer<Boolean>> solvedObservers;

  private Game game;
  private Guess guess;
  private Boolean solved;
  private Throwable error;

  private GameViewModel() {
    service = CodebreakerService.getInstance();
    gameObservers = new LinkedList<>();
    guessObservers = new LinkedList<>();
    errorObservers = new LinkedList<>();
    solvedObservers = new LinkedList<>();
  }

  /**
   * Returns the singleton instance of this view model.
   *
   * @return singleton {@link GameViewModel} instance.
   */
  public static GameViewModel getInstance() {
    return Holder.INSTANCE;
  }

  /**
   * Starts a new game with the specified pool and code length.
   *
   * <p>On success, updates the current game and notifies registered game observers. Also updates
   * the solved state from the returned game and notifies solved observers.
   *
   * <p>On failure, sets the current error and notifies registered error observers.
   *
   * @param pool pool of characters used to generate codes for the new game.
   * @param length required code length for the new game.
   */
  public void startGame(String pool, int length) {
    Game game = new Game()
        .pool(pool)
        .length(length);
    service
        .startGame(game)
        .thenApply((startedGame) -> setGame(startedGame).getSolved())
        .thenAccept(this::setSolved)
        .exceptionally(this::logError);
  }

  /**
   * Retrieves a game by id and updates the current game state.
   *
   * <p>On success, updates the current game and notifies registered game observers. Also updates
   * the solved state from the returned game and notifies solved observers.
   *
   * <p>On failure, sets the current error and notifies registered error observers.
   *
   * @param gameId identifier of the game to retrieve.
   */
  public void getGame(String gameId) {
    service
        .getGame(gameId)
        .thenApply((game) -> setGame(game).getSolved())
        .thenAccept(this::setSolved)
        .exceptionally(this::logError);
  }

  /**
   * Deletes a game by id.
   *
   * <p>On failure, sets the current error and notifies registered error observers.
   *
   * @param gameId identifier of the game to delete.
   */
  public void deleteGame(String gameId) {
    service
        .deleteGame(gameId)
        .exceptionally(this::logError);
  }

  /**
   * Deletes the currently loaded game.
   *
   * <p>On success, clears the current game and notifies registered game observers with {@code null}.
   *
   * <p>On failure, sets the current error and notifies registered error observers.
   */
  public void deleteGame() {
    service
        .deleteGame(game.getId())
        .thenRun(() -> setGame(null))
        .exceptionally(this::logError);
  }

  /**
   * Submits a guess for the currently loaded game.
   *
   * <p>On success, updates the current guess and notifies registered guess observers. If the
   * returned guess indicates a solution, refreshes the current game by retrieving it from the
   * service; otherwise, adds the guess to the current game's guess list and notifies game observers.
   *
   * <p>On failure, sets the current error and notifies registered error observers.
   *
   * @param text guess text to submit.
   */
  public void submitGuess(String text) {
    Guess guess = new Guess()
        .text(text);
    service
        .submitGuess(game, guess)
        .thenApply(this::setGuess)
        .thenAccept((guessResponse) -> {
          if (Boolean.TRUE.equals(guessResponse.getSolution())) {
            getGame(game.getId());
          } else {
            //noinspection DataFlowIssue
            game.getGuesses().add(guessResponse);
            setGame(game);
          }
        })
        .exceptionally(this::logError);
  }

  /**
   * Retrieves a guess by id for the currently loaded game.
   *
   * <p>On success, updates the current guess and notifies registered guess observers.
   *
   * <p>On failure, sets the current error and notifies registered error observers.
   *
   * @param guessId identifier of the guess to retrieve.
   */
  public void getGuess(String guessId) {
    service
        .getGuess(game.getId(), guessId)
        .thenAccept(this::setGuess)
        .exceptionally(this::logError);
  }

  /**
   * Releases resources held by the underlying service client.
   */
  public void shutdown() {
    service.shutdown();
  }

  /**
   * Registers an observer that is notified when the current game changes.
   *
   * <p>If a current game is already available, the observer is invoked immediately with the
   * current value.
   *
   * @param observer consumer invoked with updated {@link Game} values.
   */
  public void registerGameObserver(Consumer<Game> observer) {
    gameObservers.add(observer);
    if (game != null) {
      observer.accept(game);
    }
  }

  /**
   * Registers an observer that is notified when the current guess changes.
   *
   * <p>If a current guess is already available, the observer is invoked immediately with the
   * current value.
   *
   * @param observer consumer invoked with updated {@link Guess} values.
   */
  public void registerGuessObserver(Consumer<Guess> observer) {
    guessObservers.add(observer);
    if (guess != null) {
      observer.accept(guess);
    }
  }

  /**
   * Registers an observer that is notified when the solved state changes.
   *
   * <p>If a solved state is already available, the observer is invoked immediately with the
   * current value.
   *
   * @param observer consumer invoked with updated solved-state values.
   */
  public void registerSolvedObserver(Consumer<Boolean> observer) {
    solvedObservers.add(observer);
    if (solved != null) {
      observer.accept(solved);
    }
  }

  /**
   * Registers an observer that is notified when an error is recorded.
   *
   * <p>If an error is already available, the observer is invoked immediately with the current
   * value.
   *
   * @param observer consumer invoked with updated {@link Throwable} values.
   */
  public void registerErrorObserver(Consumer<Throwable> observer) {
    errorObservers.add(observer);
    if (error != null) {
      observer.accept(error);
    }
  }

  private Game setGame(Game game) {
    this.game = game;

    Platform.runLater(() -> gameObservers
        .forEach((consumer) -> consumer.accept(game)));

    return game;
  }

  private Guess setGuess(Guess guess) {
    this.guess = guess;

    Platform.runLater(() -> guessObservers
        .forEach((consumer) -> consumer.accept(guess)));

    return guess;
  }

  private Boolean setSolved(Boolean solved) {
    this.solved = solved;

    Platform.runLater(() -> solvedObservers
        .forEach((consumer) -> consumer.accept(solved)));

    return solved;
  }

  private Throwable setError(Throwable error) {
    this.error = error;

    Platform.runLater(() -> errorObservers
        .forEach((consumer) -> consumer.accept(error)));

    return error;
  }

  private Void logError(Throwable error) {
    //noinspection ThrowableNotThrown
    setError(error.getCause() != null ? error.getCause() : error);
//    this.error.printStackTrace();
    return null;
  }

  private static class Holder {

    static final GameViewModel INSTANCE = new GameViewModel();

  }

}
