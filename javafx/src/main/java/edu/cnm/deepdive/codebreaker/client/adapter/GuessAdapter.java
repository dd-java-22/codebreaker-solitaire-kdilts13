package edu.cnm.deepdive.codebreaker.client.adapter;

import edu.cnm.deepdive.codebreaker.api.model.Guess;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class GuessAdapter implements Callback<ListView<Guess>, ListCell<Guess>> {

  private static final String GUESS_HISTORY_LAYOUT_KEY = "guess_history_layout";
  private static final String GUESS_CHARACTER_LAYOUT_KEY = "guess_character_layout";

  private final ResourceBundle resources;
  private final URL item_layout_location;
  private final URL character_layout_location;

  public GuessAdapter(ResourceBundle resources) {
    this.resources = resources;
    item_layout_location = getClass().getResource(resources.getString(GUESS_HISTORY_LAYOUT_KEY));
    character_layout_location = getClass().getResource(resources.getString(GUESS_CHARACTER_LAYOUT_KEY));
  }

  @Override
  public ListCell<Guess> call(ListView<Guess> guessListView) {
    try {
      return new GuessCell(guessListView);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private class GuessCell extends ListCell<Guess> {

    @FXML
    private HBox guessContent;

    @FXML
    private HBox guessScore;

    @FXML
    private Text exactCount;

    @FXML
    private Text nearCount;

    private final ListView<Guess> listView;
    private final Parent root;

    private GuessCell(ListView<Guess> listView)
        throws IOException {
      this.listView = listView;

      FXMLLoader loader = new FXMLLoader(item_layout_location, resources);
      loader.setController(this);
      root = loader.load();

      setText(null);
      setGraphic(null);
    }

    @Override
    protected void updateItem(Guess guess, boolean empty) {
      super.updateItem(guess, empty);

      if (empty || guess == null) {
        setGraphic(null);
      } else {
        // TODO: 2/19/2026 manipulate nodes referenced by @FXML annotated fields to present model state

        exactCount.setText(String.valueOf(guess.getExactMatches()));
        nearCount.setText(String.valueOf(guess.getNearMatches()));

        setGraphic(root);
      }
    }
  }
}
