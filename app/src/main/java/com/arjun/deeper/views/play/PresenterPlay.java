package com.arjun.deeper.views.play;

import android.os.Bundle;

import com.arjun.deeper.baseclasses.BasePresenter;
import com.arjun.deeper.utils.CommonLib;
import com.arjun.deeper.utils.DbWrapper;
import com.arjun.deeper.utils.Timer;
import com.arjun.deeper.utils.UiUtils;
import com.arjun.deeper.views.Cell;

import java.util.List;

import carbon.view.View;

public class PresenterPlay extends BasePresenter<InterfacePlay.IView> implements InterfacePlay.IPresenter {

    private final long GAME_START_TIME_MS = 10000;
    private final int LEVEL_STEPS = 3;
    private final int DIFFICULTY_STEPS = 3;

    private int stage;
    private int levelStepsCount;
    private int difficulty;
    private boolean enableRotatedCells;

    private int maxCount;
    private boolean isRunning = false;
    private int score;
    private int highScore;

    private Timer timer;

    private List<Cell> children;

    public PresenterPlay(InterfacePlay.IView view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        setupTimer();
    }

    @Override
    public void onCreateView(Bundle bundle, InterfacePlay.IView view, android.view.View fragmentContainer) {
        super.onCreateView(bundle, view, fragmentContainer);
        highScore = DbWrapper.getInt(CommonLib.Keys.HIGH_SCORE, 0);
        view.updateHighScore(highScore);
    }

    private void setupTimer() {
        timer = new Timer(new Timer.TimerCallback() {
            @Override
            public void onTick(long timeLeft) {
                updateTimeLeft();
            }

            @Override
            public void onFinish() {
                endGame();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void startGame() {
        isRunning = true;
        view.setButtonVisibility(View.GONE);
        reset();
        randomizeViews();
        timer.start(GAME_START_TIME_MS);
    }

    private void endGame() {
        isRunning = false;
        view.setButtonVisibility(View.VISIBLE);
        updateTimeLeft();
        checkHighScore();
    }

    private void checkHighScore() {
        if (score > highScore) {
            highScore = score;
            view.updateHighScore(highScore);
            DbWrapper.getInstance().save(CommonLib.Keys.HIGH_SCORE, highScore).close();
            UiUtils.showToast("New high score!");
        } else {
            UiUtils.showToast("Game over!");
        }
    }

    private void randomizeViews() {
        maxCount = 0;

        for (Cell child : children) {
            int childCount = CommonLib.getRandomIntBetween(Math.min(1 + difficulty/2, 5), Math.max(7, (9 - difficulty / 2)));
//            int childCount = CommonLib.getRandomIntBetween(1, 10);
            if (childCount > maxCount) {
                maxCount = childCount;
            }
            child.showChildren(childCount);
            if (enableRotatedCells) child.setRotation(CommonLib.getRandomBoolean() ? 0 : 90);
        }
    }

    private void reset() {
        timer.stop();
        timer.setTimeLeft(GAME_START_TIME_MS);
        updateTimeLeft();
        view.updateScore(score = 0);
        view.updateHighScore(highScore);
        stage = 1;
        difficulty = 1;
        levelStepsCount = 0;
        enableRotatedCells = false;
        for (Cell child : children) {
            child.setRotation(0);
            child.resetAttributes();
        }
    }

    private void updateTimeLeft() {
        float timeLeftDecimal = (timer.getTimeLeft() / 100) / 10F;
        String timeLeftString = String.valueOf(timeLeftDecimal);
        int length = timeLeftString.length();
        switch (length) {
            case 1:
                timeLeftString += ".0";
                break;
        }
        view.setTimeLeft(timeLeftString);
    }

    @Override
    public void setChildren(List<Cell> children) {
        this.children = children;
    }

    @Override
    public void cellClicked(Cell child) {
        if (isCorrectCell(child)) {
            addBonusTime();
            increaseDifficulty();
            view.updateScore(++score);
            randomizeViews();
        } else {
            deductPenaltyTime();
        }
    }

    private void increaseDifficulty() {
        if (++levelStepsCount == LEVEL_STEPS) {
            levelStepsCount = 0;
            if (++difficulty % DIFFICULTY_STEPS == 0) {
                switch (++stage) {
                    case 2:
                        enableRotatedCells = true;
                        break;
                    case 3:
                        for (Cell child : children) {
                            child.setChooseRandomSubcell(true);
                        }
                        break;
                    case 4:
                        for (Cell child : children) {
                            child.setSubcellShape(Cell.SubcellShape.RANDOM);
                        }
                        break;
                    case 5:
//                        Disable this difficulty as setting to gone makes visible views abruptly expand after remaining views have finished animating (and are set to gone)
//                        for (Cell child : children) {
//                            child.setSubcellHideMode(View.GONE);
//                        }
                        break;
                }
            }
        }
    }

    private boolean isCorrectCell(Cell child) {
        return child.getChildCellCount() >= maxCount;
    }

    private void addBonusTime() {
        timer.start(timer.getTimeLeft() + 1000);
    }

    private void deductPenaltyTime() {
        timer.start(timer.getTimeLeft() - 500);
    }

    @Override
    public void buttonClicked(FragmentPlay.ButtonId buttonId) {
        switch (buttonId) {
            case PLAY:
                view.hideMenu();
                break;
            case CELL:
                if (!isRunning) startGame();
                break;
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
