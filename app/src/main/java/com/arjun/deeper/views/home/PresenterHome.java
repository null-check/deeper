package com.arjun.deeper.views.home;

import android.os.Handler;

import com.arjun.deeper.baseclasses.BasePresenter;
import com.arjun.deeper.utils.CommonLib;
import com.arjun.deeper.utils.DbWrapper;
import com.arjun.deeper.utils.Timer;
import com.arjun.deeper.utils.UiUtils;
import com.arjun.deeper.views.Cell;

import java.util.List;

import carbon.view.View;

public class PresenterHome extends BasePresenter<InterfaceHome.IActivity> implements InterfaceHome.IPresenter {

    private final long GAME_START_TIME_MS = 10000;

    private int maxCount = 0;
    private boolean isRunning = false;
    private int score = 0;
    private int highScore;

    private Timer timer;

    private List<Cell> children;

    private final Handler hideHandler = new Handler();
    private final Runnable hideRunnable = () -> {
        // Delayed removal of status and navigation bar
        view.hideStatusBar();
    };

    public PresenterHome(InterfaceHome.IActivity view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        setupTimer();
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
        delayedHide();
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide() {

        // Some older devices needs a small delay between UI widget updates and a change of the status and navigation bar.
        int UI_ANIMATION_DELAY = 300;

        hideHandler.removeCallbacks(hideRunnable);
        hideHandler.postDelayed(hideRunnable, UI_ANIMATION_DELAY);
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

    public void randomizeViews() {
        maxCount = 0;

        for (Cell child : children) {
//            int childCount = CommonLib.getRandomIntBetween(Math.min(difficulty / 2, 5), Math.max(7, (9 - difficulty / 2)));
            int childCount = CommonLib.getRandomIntBetween(1, 10);
            if (childCount > maxCount) {
                maxCount = childCount;
            }
            child.showChildren(childCount);
        }
    }

    private void reset() {
        timer.stop();
        timer.setTimeLeft(GAME_START_TIME_MS);
        updateTimeLeft();
        view.updateScore(score = 0);
        view.updateHighScore(highScore);
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
        if (child.getVisibleChildCount() >= maxCount) {
            view.updateScore(++score);
            randomizeViews();
            addBonusTime();
        } else {
            deductPenaltyTime();
        }
    }

    private void addBonusTime() {
        timer.start(timer.getTimeLeft() + 1000);
    }

    private void deductPenaltyTime() {
        timer.start(timer.getTimeLeft() - 500);
    }

    @Override
    public void buttonClicked() {
        if (!isRunning) startGame();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
