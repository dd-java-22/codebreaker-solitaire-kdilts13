package edu.cnm.deepdive.codebreaker;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import edu.cnm.deepdive.codebreaker.service.CodebreakerService;
import java.util.concurrent.CompletableFuture;

public class Main {

  static void main() {
    Game game = new Game()
        .pool("ABCDE")
        .length(2);

    CodebreakerService service = CodebreakerService.getInstance();
    CompletableFuture<Game> createGameFuture = service.startGame(game);

    createGameFuture
        .thenAccept((startedGame) -> {
          System.out.println("Created a game: " + startedGame);
          String gameId = startedGame.getId();

          CompletableFuture<Game> getGameFuture = service.getGame(gameId);

          getGameFuture
            .thenAccept(fetchedGame -> {
              System.out.println("fetched game: " + fetchedGame);

              CompletableFuture<Guess> submitGuessFuture = service.submitGuess(gameId, new Guess().text("AB"));

              submitGuessFuture
                .thenAccept(submittedGuess -> {
                  System.out.println("submitGuess: " + submittedGuess);
                })
                .exceptionally(Main::printTrace);
            })
            .exceptionally(Main::printTrace);
        })
        .exceptionally(Main::printTrace);
    System.out.println("game start requested");
  }

  private static Void printTrace(Throwable throwable) {
    throwable.printStackTrace();
    return null;
  }
}
