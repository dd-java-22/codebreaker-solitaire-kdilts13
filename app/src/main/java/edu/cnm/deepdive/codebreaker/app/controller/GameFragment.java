package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentGameBinding;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap;
import edu.cnm.deepdive.codebreaker.app.viewmodel.GameViewModel;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class GameFragment extends Fragment {

  @Inject
  SymbolMap symbolMap;

  private FragmentGameBinding binding;
  private GameViewModel gameViewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

    LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

    gameViewModel.getGame()
      .observe(lifecycleOwner, game -> {
        // TODO: 3/6/2026 handle updates to game

        binding.palette.removeAllViews();

        game.getPool().codePoints()
          .mapToObj(codePoint -> {
            // TODO inflate a layout and return inflated widget
            return (ImageButton) null;
          })
          .map(symbolWidget -> {
            // TODO set the symbol text (contentDescription and tooltip)
            return symbolWidget;
          })
          .map(symbolWidget -> {
            // TODO set the symbol drawable
            return symbolWidget;
          })
          .map(symbolWidget -> {
            // TODO set the symbol drawable's tint
            return symbolWidget;
          })
          .forEach(binding.palette::addView);
      });

    gameViewModel.getSolved()
      .observe(lifecycleOwner, solved -> {
        // TODO: 3/6/2026 handle changes to solved state of game
      });

    gameViewModel.getGuess()
      .observe(lifecycleOwner, guess -> {
        // TODO: 3/6/2026 handle updates to most recent guess
      });

    gameViewModel.getError()
      .observe(lifecycleOwner, error -> {
        // TODO: 3/6/2026 handle error
      });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

}
