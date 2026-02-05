package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.ApiClient;
import edu.cnm.deepdive.codebreaker.model.Error;
import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for DefaultApi
 */
public class DefaultApiTest {

    private DefaultApi api;

    @Before
    public void setup() {
        api = new ApiClient().createService(DefaultApi.class);
    }

    /**
     * Delete a game resource
     *
     * 
     */
    @Test
    public void deleteGameTest() {
        String gameId = null;
        // api.deleteGame(gameId);

        // TODO: test validations
    }
    /**
     * Retrieve a game resource
     *
     * 
     */
    @Test
    public void getGameTest() {
        String gameId = null;
        // Game response = api.getGame(gameId);

        // TODO: test validations
    }
    /**
     * Retrieve a specific guess resource
     *
     * 
     */
    @Test
    public void getGuessTest() {
        String gameId = null;
        String guessId = null;
        // Guess response = api.getGuess(gameId, guessId);

        // TODO: test validations
    }
    /**
     * Start a new game
     *
     * 
     */
    @Test
    public void startGameTest() {
        Game game = null;
        // Game response = api.startGame(game);

        // TODO: test validations
    }
    /**
     * Submit a new guess for a game
     *
     * 
     */
    @Test
    public void submitGuessTest() {
        String gameId = null;
        Guess guess = null;
        // Guess response = api.submitGuess(gameId, guess);

        // TODO: test validations
    }
}
