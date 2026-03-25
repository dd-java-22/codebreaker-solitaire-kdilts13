package edu.cnm.deepdive.codebreaker.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.FragmentScoped;
import edu.cnm.deepdive.codebreaker.app.databinding.ItemGameSummaryBinding;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

@FragmentScoped
public class GameSummariesAdapter extends RecyclerView.Adapter<ViewHolder> {

  private final LayoutInflater inflater;
  private final DateTimeFormatter formatter;

  private final List<GameSummary> summaries;

  @Inject
  public GameSummariesAdapter(@ActivityContext Context context) {
    inflater = LayoutInflater.from(context);
    formatter = DateTimeFormatter
      .ofLocalizedDateTime(FormatStyle.SHORT)
      .withZone(ZoneId.systemDefault());

    summaries = new ArrayList<>();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Holder(ItemGameSummaryBinding.inflate(inflater, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ((Holder) holder).bind(position);
  }

  @Override
  public int getItemCount() {
    return summaries.size();
  }

  public List<GameSummary> getGameSummaries() {
    return summaries;
  }

  private class Holder extends ViewHolder {

    private final ItemGameSummaryBinding binding;

    private Holder(@NonNull ItemGameSummaryBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    private void bind(int position) {
      GameSummary summary = summaries.get(position);

      Instant lastPlayed = summary.getLastPlayed();

      String lastPlayedText = (lastPlayed != null) ? formatter.format(lastPlayed) : formatter.format(summary.getStarted());

      if (position % 2 == 0) {
        binding.header.setBackgroundColor(0xFFFF0000);
      }

      binding.lastPlayed.setText(lastPlayedText);
      binding.poolSize.setText(String.valueOf(summary.getPoolSize()));
      binding.codeLength.setText(String.valueOf(summary.getCodeLength()));
      binding.guessCount.setText(String.valueOf(summary.getGuessCount()));
    }
  }
}
