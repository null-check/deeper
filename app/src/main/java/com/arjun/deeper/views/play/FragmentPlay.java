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
import com.arjun.deeper.interfaces.GameGridCallback;
import com.arjun.deeper.singletons.GameStateSingleton;
import com.arjun.deeper.views.customviews.Cell;
import com.arjun.deeper.views.customviews.GameGridView;
import com.arjun.deeper.views.customviews.MenuButtonView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentPlay extends Fragment implements InterfacePlay.IView {

    @BindView(R.id.game_container)
    protected ViewGroup gameContainer;

    @BindView(R.id.time_left)
    protected TextView timeLeft;

    @BindView(R.id.score_value)
    protected TextView scoreTextView;

    @BindView(R.id.high_score_value)
    protected TextView highScoreTextView;

    @BindView(R.id.hint_container)
    protected ViewGroup hintContainer;

    @BindView(R.id.hint_title)
    protected TextView hintTitle;

    @BindView(R.id.hint_message)
    protected TextView hintMessage;

    @BindView(R.id.overlay_hint_container)
    protected ViewGroup overlayHintContainer;

    @BindView(R.id.overlay_hint_text)
    protected TextView overlayHintText;

    @BindView(R.id.game_grid)
    protected GameGridView gameGridView;

    @BindView(R.id.menu_container)
    protected ViewGroup menuContainer;

    @BindView(R.id.play_button)
    protected MenuButtonView playButton;

    @BindView(R.id.restart_button)
    protected MenuButtonView restartButton;

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
        RESTART,
        TUTORIAL,
        SCOREBOARD,
        CELL,
        MENU_BG,
        HINT_OVERLAY
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
        setClickListeners();
    }

    private void setClickListeners() {
        playButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.PLAY));
        restartButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.RESTART));
        tutorialButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.TUTORIAL));
        scoreboardButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.SCOREBOARD));
        menuContainer.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.MENU_BG));
        overlayHintContainer.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.HINT_OVERLAY));
        gameGridView.setGameGridCallback(new GameGridCallback() {
            @Override
            public void cellClicked(int childCount, int maxCount, int position) {
                GameStateSingleton.GameState gameState = GameStateSingleton.getInstance().getGameState();
                if (gameState == GameStateSingleton.GameState.RUNNING
                        || gameState == GameStateSingleton.GameState.TUTORIAL)
                    presenterPlay.cellClicked(childCount, maxCount, position);
            }

            @Override
            public void cellButtonClicked() {
                presenterPlay.buttonClicked(ButtonId.CELL);
            }
        });
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
    public void onDestroy() {
        super.onDestroy();
        presenterPlay.onDestroy();
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
        gameGridView.setCellButtonVisibility(visibility);
    }

    @Override
    public void setCellButtonText(String text) {
        gameGridView.setCellButtonText(text);
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
    public void showRestartButton() {
        restartButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRestartButton() {
        restartButton.setVisibility(View.GONE);
    }

    @Override
    public void setPlayButtonText(String text) {
        playButton.setText(text);
    }

    @Override
    public void increaseLevel() {
        gameGridView.getLevelController().increaseLevel();
    }

    @Override
    public void resetLevel() {
        gameGridView.getLevelController().reset();
    }

    @Override
    public void setChildren(int[] childCounts) {
        gameGridView.setChildren(childCounts);
    }

    @Override
    public void setShowCount(boolean showCount) {
        gameGridView.setShowCount(showCount);
    }

    @Override
    public void setHintVisibility(int visibility) {
        overlayHintContainer.setVisibility(visibility);
    }

    @Override
    public void setHintTitle(String title) {
        hintTitle.setText(title);
    }

    @Override
    public void setHintMessage(String message) {
        overlayHintText.setText(message);
    }
}
