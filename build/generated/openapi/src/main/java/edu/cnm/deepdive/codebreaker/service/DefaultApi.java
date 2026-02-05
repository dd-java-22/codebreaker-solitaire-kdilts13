package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import edu.cnm.deepdive.codebreaker.model.Error;
import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DefaultApi {
  /**
   * Delete a game resource
   * 
   * @param gameId  (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("games/{gameId}")
  Call<Void> deleteGame(
    @retrofit2.http.Path("gameId") String gameId
  );

  /**
   * Retrieve a game resource
   * 
   * @param gameId  (required)
   * @return Call&lt;Game&gt;
   */
  @GET("games/{gameId}")
  Call<Game> getGame(
    @retrofit2.http.Path("gameId") String gameId
  );

  /**
   * Retrieve a specific guess resource
   * 
   * @param gameId  (required)
   * @param guessId  (required)
   * @return Call&lt;Guess&gt;
   */
  @GET("games/{gameId}/guesses/{guessId}")
  Call<Guess> getGuess(
    @retrofit2.http.Path("gameId") String gameId, @retrofit2.http.Path("guessId") String guessId
  );

  /**
   * Start a new game
   * 
   * @param game  (required)
   * @return Call&lt;Game&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("games")
  Call<Game> startGame(
    @retrofit2.http.Body Game game
  );

  /**
   * Submit a new guess for a game
   * 
   * @param gameId  (required)
   * @param guess  (required)
   * @return Call&lt;Guess&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("games/{gameId}/guesses")
  Call<Guess> submitGuess(
    @retrofit2.http.Path("gameId") String gameId, @retrofit2.http.Body Guess guess
  );

}
