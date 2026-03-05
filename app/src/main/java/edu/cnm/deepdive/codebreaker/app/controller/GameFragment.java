package edu.cnm.deepdive.codebreaker.app.controller;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.app.adapter.GuessAdapter;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentGameBinding;
import edu.cnm.deepdive.codebreaker.app.viewmodel.GameViewModel;
import java.util.Arrays;
import java.util.stream.Collectors;

@AndroidEntryPoint
public class GameFragment extends Fragment {

  private FragmentGameBinding binding;
  private GameViewModel viewModel;
  private String[] currentGuess;
  private int activeIndex;
  private int gameLength;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    binding.newGame.setOnClickListener((v) -> {
      viewModel.startGame("ROYGBIV", 4); // Default pool and length
      // TODO: 3/5/2026 update to get pool and length from game
      clearCurrentGuess();
    });
    binding.submit.setOnClickListener((v) -> {
      String guessText = Arrays.stream(currentGuess).collect(Collectors.joining());
      viewModel.submitGuess(guessText);
      clearCurrentGuess();
    });
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
    viewModel
        .getGame()
        .observe(getViewLifecycleOwner(), this::handleGameChange);
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void handleGameChange(Game game) {
    if (game != null) {
      gameLength = game.getLength();
      if (currentGuess == null || currentGuess.length != gameLength) {
        currentGuess = new String[gameLength];
        Arrays.fill(currentGuess, " ");
        activeIndex = 0;
      }
      binding.guessHistory.setAdapter(new GuessAdapter(game.getGuesses(), getLayoutInflater()));
      binding.guessHistory.scrollToPosition(game.getGuesses().size() - 1);
      setupPalette(game.getPool());
      updateCurrentGuessDisplay();
    }
  }

  private void setupPalette(String pool) {
    binding.palette.removeAllViews();
    Context context = requireContext();
    pool.codePoints()
        .forEach((codePoint) -> {
          String symbol = new String(Character.toChars(codePoint));
          TextView tv = new TextView(context);
          tv.setText(symbol);
          tv.setTextSize(32);
          tv.setPadding(16, 16, 16, 16);
          tv.setOnClickListener((v) -> {
            currentGuess[activeIndex] = symbol;
            if (activeIndex < gameLength - 1) {
              activeIndex++;
            }
            updateCurrentGuessDisplay();
          });
          binding.palette.addView(tv);
        });
  }

  private void updateCurrentGuessDisplay() {
    binding.currentGuessDisplay.removeAllViews();
    Context context = requireContext();
    for (int i = 0; i < gameLength; i++) {
      int index = i;
      TextView tv = new TextView(context);
      tv.setText(currentGuess[i]);
      tv.setTextSize(32);
      tv.setPadding(16, 16, 16, 16);
      if (i == activeIndex) {
        tv.setBackgroundColor(Color.LTGRAY);
      }
      tv.setOnClickListener((v) -> {
        activeIndex = index;
        updateCurrentGuessDisplay();
      });
      binding.currentGuessDisplay.addView(tv);
    }
    long filledCount = Arrays.stream(currentGuess).filter(s -> !s.equals(" ")).count();
    binding.submit.setEnabled(filledCount == gameLength);
  }

  private void clearCurrentGuess() {
    if (currentGuess != null) {
      Arrays.fill(currentGuess, " ");
      activeIndex = 0;
      updateCurrentGuessDisplay();
    }
  }

}
