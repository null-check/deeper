package com.arjun.deeper.views.play;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arjun.deeper.R;
import com.arjun.deeper.singletons.GameStateSingleton;
import com.arjun.deeper.views.Cell;
import com.arjun.deeper.views.MenuButtonView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.LinearLayout;

public class FragmentPlay extends Fragment implements InterfacePlay.IView {

    @BindView(R.id.root_view)
    protected ViewGroup rootView;

    @BindView(R.id.game_container)
    protected ViewGroup gameContainer;

    @BindView(R.id.time_left)
    protected TextView timeLeft;

    @BindView(R.id.score_value)
    protected TextView scoreTextView;

    @BindView(R.id.high_score_value)
    protected TextView highScoreTextView;

    @BindView(R.id.grid_root)
    protected LinearLayout gridRoot;

    @BindView(R.id.cell_button)
    protected TextView cellButton;

    @BindView(R.id.menu_container)
    protected ViewGroup menuContainer;

    @BindView(R.id.play_button)
    protected MenuButtonView playButton;

    @BindView(R.id.tutorial_button)
    protected MenuButtonView tutorialButton;

    @BindView(R.id.scoreboard_button)
    protected MenuButtonView scoreboardButton;

    @BindView(R.id.cell_1) protected Cell cell1;
    @BindView(R.id.cell_2) protected Cell cell2;
    @BindView(R.id.cell_3) protected Cell cell3;
    @BindView(R.id.cell_4) protected Cell cell4;
    @BindView(R.id.cell_5) protected Cell cell5;
    @BindView(R.id.cell_6) protected Cell cell6;
    @BindView(R.id.cell_7) protected Cell cell7;
    @BindView(R.id.cell_8) protected Cell cell8;
    @BindView(R.id.cell_9) protected Cell cell9;

    enum ButtonId {
        PLAY,
        TUTORIAL,
        SCOREBOARD,
        CELL,
        MENU_BG
    }

    private PresenterPlay presenterPlay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterPlay = new PresenterPlay(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        ButterKnife.bind(this, rootView);
        presenterPlay.onCreateView(savedInstanceState, this, container);
        setupView();

        return rootView;
    }

    private void setupView() {
        List<Cell> children = new ArrayList(Arrays.asList(cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9));
        presenterPlay.setChildren(children);
        setClickListeners(children);
    }

    private void setClickListeners(List<Cell> children) {
        for (Cell child : children) {
            child.setOnClickListener(view -> {
                if (GameStateSingleton.getInstance().getGameState() == GameStateSingleton.GameState.RUNNING) presenterPlay.cellClicked(child);
            });
        }

        playButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.PLAY));
        tutorialButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.TUTORIAL));
        scoreboardButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.SCOREBOARD));
        cellButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.CELL));
        menuContainer.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.MENU_BG));
    }

    @Override
    public void onResume() {
        super.onResume();
        presenterPlay.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenterPlay.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenterPlay.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenterPlay.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        presenterPlay.onSaveInstanceState(outState);
    }

    @Override
    public void setTimeLeft(String timeLeftString) {
        timeLeft.setText(timeLeftString);
    }

    @Override
    public void updateScore(int score) {
        scoreTextView.setText(String.valueOf(score));
    }

    @Override
    public void updateHighScore(int score) {
        highScoreTextView.setText(String.valueOf(score));
    }

    @Override
    public void setCellButtonVisibility(int visibility) {
        cellButton.setVisibility(visibility);
    }

    @Override
    public void setCellButtonText(String text) {
        cellButton.setText(text);
    }

    @Override
    public void showGame() {
        gameContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMenu() {
        menuContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMenu() {
        menuContainer.setVisibility(View.GONE);
    }

    @Override
    public void setPlayButtonText(String text) {
        playButton.setText(text);
    }
}
