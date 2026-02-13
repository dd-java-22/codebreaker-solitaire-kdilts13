package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import java.util.concurrent.CompletableFuture;

/**
 * Client-side access to the Codebreaker Solitaire service for creating, retrieving, deleting games,
 * and submitting/retrieving guesses.
 *
 * <p>All operations are asynchronous and return {@link CompletableFuture} instances that complete
 * when the underlying service request succeeds or fails.
 *
 * <p>The singleton returned by {@link #getInstance()} completes returned futures exceptionally in
 * these cases:
 *
 * <ul>
 *   <li>{@link InvalidPayloadException} when a request payload fails client-side validation or when
 *       the service responds with HTTP 400.
 *   <li>{@link ResourceNotFoundException} when the service responds with HTTP 404.
 *   <li>{@link GameSolvedException} when the service responds with HTTP 409.
 *   <li>{@link UnknownServiceException} when the service responds with HTTP 500 or with any
 *       unrecognized non-success HTTP status.
 * </ul>
 */
public interface CodebreakerService {

  /**
   * Returns the default singleton implementation of this service client.
   *
   * @return singleton {@link CodebreakerService} instance.
   */
  static CodebreakerService getInstance() {
    return CodebreakerServiceImpl.getInstance();
  }

  /**
   * Starts a new game using the provided game configuration.
   *
   * <p>The singleton returned by {@link #getInstance()} performs client-side validation of the
   * {@code game} configuration and completes the returned future exceptionally with
   * {@link InvalidPayloadException} if the configuration is not valid.
   *
   * @param game game configuration used to start a new game.
   * @return future completing with the newly created {@link Game}.
   */
  CompletableFuture<Game> startGame(Game game);

  /**
   * Retrieves a game by id.
   *
   * @param gameId identifier of the game to retrieve.
   * @return future completing with the requested {@link Game}.
   */
  CompletableFuture<Game> getGame(String gameId);

  /**
   * Deletes a game by id.
   *
   * @param gameId identifier of the game to delete.
   * @return future completing when deletion succeeds.
   */
  CompletableFuture<Void> deleteGame(String gameId);

  /**
   * Submits a guess for the specified game.
   *
   * <p>The singleton returned by {@link #getInstance()} performs client-side validation of the
   * {@code guess} with respect to the specified {@code game} and completes the returned future
   * exceptionally with {@link InvalidPayloadException} if the guess is not valid.
   *
   * @param game game for which the guess is being submitted.
   * @param guess guess to submit.
   * @return future completing with the created {@link Guess}.
   */
  CompletableFuture<Guess> submitGuess(Game game, Guess guess);

  /**
   * Retrieves a guess by game id and guess id.
   *
   * @param gameId identifier of the game that contains the guess.
   * @param guessId identifier of the guess to retrieve.
   * @return future completing with the requested {@link Guess}.
   */
  CompletableFuture<Guess> getGuess(String gameId, String guessId);

  /**
   * Releases resources held by the service client (e.g., request execution resources).
   */
  void shutdown();

}
