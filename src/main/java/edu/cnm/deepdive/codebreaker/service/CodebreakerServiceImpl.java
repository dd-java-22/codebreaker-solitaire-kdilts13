package edu.cnm.deepdive.codebreaker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class CodebreakerServiceImpl implements CodebreakerService {

  private static final String PROPERTIES_FILE = "service.properties";
  private static final String LOG_LEVEL_KEY = "logLevel";
  private static final String BASE_URL_KEY = "baseUrl";

  private static final Map<Integer, Supplier<Throwable>> CODE_THROWABLE_SUPPLIERS = Map.ofEntries(
    Map.entry(400, IllegalArgumentException::new),
    Map.entry(404, NoSuchElementException::new),
    Map.entry(409, IllegalStateException::new)
  );

  private final CodebreakerApi api;

  private CodebreakerServiceImpl() {
    Properties properties = loadProperties();
    Gson gson = buildGson();
    OkHttpClient client = buildClient(properties);
    api = buildApi(properties, gson, client);
  }

  static CodebreakerServiceImpl getInstance() {
    return Holder.INSTANCE;
  }

  @Override
  public CompletableFuture<Game> startGame(Game game) {
    return isValidGame(game)
        ? buildStartGameFuture(game)
        : CompletableFuture.failedFuture(new IllegalArgumentException());
  }

  @Override
  public CompletableFuture<Game> getGame(String gameId) {
    CompletableFuture<Game> future = new CompletableFuture<>();

    api
        .getGame(gameId)
        .enqueue(new ServiceCallback<>(future));

    return future;
  }

  @Override
  public CompletableFuture<Void> delete(String gameId) {
    CompletableFuture<Void> future = new CompletableFuture<>();

    api
        .deleteGame(gameId)
        .enqueue(new ServiceCallback<>(future));

    return future;
  }

  @Override
  public CompletableFuture<Guess> submitGuess(Game game, Guess guess) {
    return isValidGuess(game, guess)
        ? buildSubmitGuessFuture(game, guess)
        : CompletableFuture.failedFuture(new IllegalArgumentException());
  }

  @Override
  public CompletableFuture<Guess> getGuess(String gameId, String guessId) {
    CompletableFuture<Guess> future = new CompletableFuture<>();

    api.getGuess(gameId, guessId)
        .enqueue(new ServiceCallback<>(future));

    return future;
  }

  //<editor-fold desc="Validation methods" defaultMode="collapsed">
  private static boolean isValidGame(Game game) {
    boolean isValidLength = game.getLength() > 0 && game.getLength() <= 20;

    // Used AI to generate the stream logic to check that the pool only
    // uses valid Unicode characters with no null or unassigned Unicode code points.
    boolean containsOnlyValidChars =
        game.getPool().codePoints()
            .allMatch(cp -> cp != 0 && Character.isDefined(cp));

    return isValidLength && containsOnlyValidChars;
  }

  private static boolean isValidGuess(Game game, Guess guess) {
    boolean isCorrectLength = guess.getText().length() == game.getLength();

    // Used AI to generate the stream logic to check the guess characters
    boolean containsOnlyValidChars =
        guess.getText().chars().allMatch(ch -> game.getPool().indexOf(ch) >= 0);

    return isCorrectLength && containsOnlyValidChars;
  }
  //</editor-fold>

  private static Gson buildGson() {
    return new GsonBuilder()
        .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
        .create();
  }

  private static OkHttpClient buildClient(Properties properties) {
    Interceptor interceptor = new HttpLoggingInterceptor()
        .setLevel(Level.valueOf(properties.getProperty(LOG_LEVEL_KEY).toUpperCase()));
    return new OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build();
  }

  private static CodebreakerApi buildApi(Properties properties, Gson gson, OkHttpClient client) {
    return new Retrofit.Builder()
        .baseUrl(properties.getProperty(BASE_URL_KEY))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
        .create(CodebreakerApi.class);
  }

  private static Properties loadProperties() {
    Properties properties = new Properties();
    try (InputStream input =
        CodebreakerServiceImpl.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
      properties.load(input);
      return properties;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  private CompletableFuture<Game> buildStartGameFuture(Game game) {
    CompletableFuture<Game> future = new CompletableFuture<>();

    api
        .startGame(game)
        .enqueue(new ServiceCallback<>(future));

    return future;
  }

  @NotNull
  private CompletableFuture<Guess> buildSubmitGuessFuture(Game game, Guess guess) {
    CompletableFuture<Guess> future;
    future = new CompletableFuture<>();

    api.submitGuess(game.getId(), guess)
        .enqueue(new ServiceCallback<>(future));
    return future;
  }

  private static class OffsetDateTimeAdapter extends TypeAdapter<OffsetDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, OffsetDateTime offsetDateTime) throws IOException {
      jsonWriter.jsonValue(offsetDateTime != null ? offsetDateTime.toString() : null);
    }

    @Override
    public OffsetDateTime read(JsonReader jsonReader) throws IOException {
      return OffsetDateTime.parse(jsonReader.nextString());
    }

  }

  private static class Holder {

    static final CodebreakerServiceImpl INSTANCE = new CodebreakerServiceImpl();

  }

  private static class ServiceCallback<T> implements Callback<T> {
    private final CompletableFuture<T> future;

    private ServiceCallback(CompletableFuture<T> future) {
      this.future = future;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
      if (response.isSuccessful()) {
        future.complete(response.body());
      } else {
        future.completeExceptionally(CODE_THROWABLE_SUPPLIERS.getOrDefault(
            response.code(),
            RuntimeException::new
        ).get());
      }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {

    }
  }
}
