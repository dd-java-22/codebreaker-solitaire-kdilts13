package edu.cnm.deepdive.codebreaker.app.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.client.service.CodebreakerService;
import jakarta.inject.Inject;

@HiltViewModel
public class GameViewModel extends ViewModel {

  private static final String TAG = GameViewModel.class.getSimpleName();

  private final CodebreakerService service;

  private final MutableLiveData<Game> game;
  private final MutableLiveData<Guess> guess;
  private final MutableLiveData<Throwable> error;
  private final LiveData<Boolean> solved;

  @Inject
  GameViewModel(CodebreakerService service) {
    this.service = service;

    game = new MutableLiveData<>();
    guess = new MutableLiveData<>();
    error = new MutableLiveData<>();

    solved = Transformations.distinctUntilChanged(
      Transformations.map(
        game,
        Game::getSolved
      )
    );
  }

  public void startGame(String pool, int length) {
    Game game = new Game().pool(pool).length(length);

    service.startGame(game)
      .thenAccept(this.game::postValue)
      .exceptionally(this::postThrowable);
  }

  public void getGame(String gameId) {
    service.getGame(gameId)
      .thenAccept(this.game::postValue)
      .exceptionally(this::postThrowable);
  }

  public void deleteGame() {
    Game game = this.game.getValue();
    this.game.setValue(null);

    if (game != null) {
      service.deleteGame(game.getId())
        .exceptionally(this::postThrowable);
    }
  }

  public void getGuess(String guessId) {
    Game game = this.game.getValue();

    //noinspection DataFlowIssue
    service.getGuess(game.getId(), guessId)
      .thenAccept(this.guess::postValue)
      .exceptionally(this::postThrowable);
  }

  @SuppressWarnings("DataFlowIssue")
  public void submitGuess(String guessText) {
    Guess guess = new Guess().text(guessText);
    Game game = this.game.getValue();

    service.submitGuess(game, guess)
      .thenApply(g -> {
        this.guess.postValue(g);
        return g;
      })
      .thenAccept(g -> {
        if (Boolean.TRUE.equals(g.getSolution())) {
          getGame(game.getId());
        } else {
          game.getGuesses().add(g);
          this.game.postValue(game);
        }
      })
      .exceptionally(this::postThrowable);
  }

  public LiveData<Game> getGame() {
    return game;
  }

  public LiveData<Guess> getGuess() {
    return guess;
  }

  public LiveData<Throwable> getError() {
    return error;
  }

  public LiveData<Boolean> getSolved() {
    return solved;
  }

  private Void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    error.postValue(throwable);
    return null;
  }
}
