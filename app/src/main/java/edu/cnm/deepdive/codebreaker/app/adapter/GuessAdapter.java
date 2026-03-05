package edu.cnm.deepdive.codebreaker.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.databinding.ItemGuessBinding;
import java.util.List;

public class GuessAdapter extends RecyclerView.Adapter<GuessAdapter.ViewHolder> {

  private final List<Guess> guesses;
  private final LayoutInflater inflater;

  public GuessAdapter(List<Guess> guesses, LayoutInflater inflater) {
    this.guesses = guesses;
    this.inflater = inflater;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(ItemGuessBinding.inflate(inflater, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind(guesses.get(position));
  }

  @Override
  public int getItemCount() {
    return guesses.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    private final ItemGuessBinding binding;

    ViewHolder(ItemGuessBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    void bind(Guess guess) {
      binding.guessText.setText(guess.getText());
      //noinspection DataFlowIssue
      binding.results.setMatches(guess.getExactMatches(), guess.getNearMatches());
    }
  }
}
