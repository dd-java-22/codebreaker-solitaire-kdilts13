package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public interface CodebreakerService {

  /**
   *
   * @return
   */
  static CodebreakerService getInstance() {
    return CodebreakerServiceImpl.getInstance();
  }

  /**
   *
   * @param game
   * @return
   */
  CompletableFuture<Game> startGame(Game game);

  /**
   *
   * @param gameId
   * @return
   */
  CompletableFuture<Game> getGame(String gameId);

  /**
   *
   * @param gameId
   * @return
   */
  CompletableFuture<Void> deleteGame(String gameId);

  /**
   *
   * @param game
   * @param guess
   * @return
   */
  CompletableFuture<Guess> submitGuess(Game game, Guess guess);

  /**
   *
   * @param gameId
   * @param guessId
   * @return
   */
  CompletableFuture<Guess> getGuess(String gameId, String guessId);

  /**
   *
   */
  void shutdown();

}
