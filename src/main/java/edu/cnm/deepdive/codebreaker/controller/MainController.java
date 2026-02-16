package edu.cnm.deepdive.codebreaker.controller;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainController {

  private static final String POOL_KEY = "pool";
  private static final String LENGTH_KEY = "length";
  private static final Pattern PROPERTY_LIST_DELIMITER = Pattern.compile("\\s*,\\s*");
  private static final String POOL_NAMES_KEY = "pool_names";
  private static final String POOL_CLASSES_KEY = "pool_classes";
  private static final String GUESS_PALETTE_LAYOUT_KEY = "palette_item_layout";

  @FXML
  private ResourceBundle resources;

  @FXML
  private ScrollPane scrollPane;

  @FXML
  private TextFlow textFlow;

  @FXML
  private Text gameState;

  @FXML
  private TilePane guessContainer;

  @FXML
  private Button send;

  @FXML
  private TilePane guessPalette;

  private GameViewModel viewModel;
  private Game game;
  private Map<Integer, String> codePointNames;
  private Map<Integer, String> codePointClasses;

  @FXML
  private void initialize() throws IOException {
    buildCodePointMaps();

    viewModel = connectToViewModel();
    startGame();
  }

  private void buildCodePointMaps() {
    List<Integer> poolCodePoints = resources
        .getString(POOL_KEY)
        .codePoints()
        .boxed()
        .toList();

    List<String> poolNames = buildPoolMap(POOL_NAMES_KEY);

    List<String> poolClasses = buildPoolMap(POOL_CLASSES_KEY);

    codePointNames = new LinkedHashMap<>();
    codePointClasses = new LinkedHashMap<>();

    Iterator<Integer> codePointsIter = poolCodePoints.iterator();
    Iterator<String> namesIter = poolNames.iterator();
    Iterator<String> classIter = poolClasses.iterator();

    while (codePointsIter.hasNext() && namesIter.hasNext() && classIter.hasNext()) {
      Integer codePoint = codePointsIter.next();
      codePointNames.put(codePoint, namesIter.next());
      codePointClasses.put(codePoint, classIter.next());
    }
  }

  private List<String> buildPoolMap(String key) {
    return PROPERTY_LIST_DELIMITER.splitAsStream(
            resources
                .getString(key)
        )
        .filter(Predicate.not(String::isBlank))
        .toList();
  }

  @FXML
  private void submitGuess() {
//    String guessText = guessInput.getText().strip();
//
//    if (guessText.length() == game.getLength()) {
//      viewModel.submitGuess(guessText);
//    }
  }

  private GameViewModel connectToViewModel() {
    GameViewModel viewModel = GameViewModel.getInstance();

    viewModel.registerGameObserver(this::handleGame);
    viewModel.registerErrorObserver((throwable) -> { /* TODO display or log this throwable */ });

    return viewModel;
  }

  private void handleGame(Game game) {
    this.game = game;
    gameState.setText(game.toString());
    EventHandler<ActionEvent> handler = (event) ->
        System.out.println(((Node) event.getSource()).getUserData());

    ObservableList<Node> children = guessPalette.getChildren();
    children.clear();

    URL layoutURL = getClass()
        .getClassLoader()
        .getResource(resources.getString(GUESS_PALETTE_LAYOUT_KEY));

    codePointClasses
        .entrySet()
        .stream()
        .map((entry) -> {
          try {
            Labeled node = new FXMLLoader(layoutURL, resources).load();
            node.getStyleClass().add(entry.getValue());

            Integer key = entry.getKey();
            String name = codePointNames.get(key);

            node.addEventHandler(ActionEvent.ACTION, handler);
            node.setTooltip(new Tooltip(name));
            node.setText(buildSingleCharacterMnemonicLabel(name));
            node.setUserData(key);

            return node;
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .forEach(children::add);
  }

  private String buildSingleCharacterMnemonicLabel(String name) {
    return IntStream.concat(
            IntStream.of('_'),
            name.codePoints().limit(1)
        )
        .boxed()
        .reduce(new StringBuilder(), StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  private void startGame() throws IOException {
    String pool = resources.getString(POOL_KEY);
    int length = Integer.parseInt(resources.getString(LENGTH_KEY));
    viewModel.startGame(pool, length);
  }
}
