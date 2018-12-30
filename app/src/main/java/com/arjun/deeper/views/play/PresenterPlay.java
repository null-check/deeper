package com.arjun.deeper.views.play;

import android.os.Bundle;
import android.os.CountDownTimer;

import com.arjun.deeper.R;
import com.arjun.deeper.baseclasses.BasePresenter;
import com.arjun.deeper.events.BackpressEvent;
import com.arjun.deeper.singletons.GameStateSingleton;
import com.arjun.deeper.utils.CommonLib;
import com.arjun.deeper.utils.DbWrapper;
import com.arjun.deeper.utils.StringUtils;
import com.arjun.deeper.utils.Timer;
import com.arjun.deeper.utils.UiUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import carbon.view.View;

public class PresenterPlay extends BasePresenter<InterfacePlay.IView> implements InterfacePlay.IPresenter {

    private final float GAME_START_TIME = 10f;
    private final long INTRO_COUNTDOWN = 3;
    private final float INTRO_SPEED = 2f;
    private final float TIME_BONUS = 1f;
    private final float TIME_PENALTY = 0.5f;

    private int score;
    private int highScore;

    private Timer timer;

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
        unpackBundle(bundle);
        highScore = DbWrapper.getInt(CommonLib.Keys.HIGH_SCORE, 0);
        view.updateHighScore(highScore);
    }

    @Override
    public void unpackBundle(Bundle bundle) {
        super.unpackBundle(bundle);
        if (bundle != null) {
            timer.setTimeLeft(bundle.getLong(CommonLib.Keys.TIME_LEFT));
            score = bundle.getInt(CommonLib.Keys.SCORE);
        }
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

    private void startGame() {
        setGameState(GameStateSingleton.GameState.RUNNING);
        view.showRestartButton();
        view.setCellButtonVisibility(View.GONE);
        reset();
        timer.start(GAME_START_TIME);
    }

    private void endGame() {
        setGameState(GameStateSingleton.GameState.OVER);
        view.hideRestartButton();
        view.setPlayButtonText(StringUtils.getString(R.string.play));
        view.setCellButtonVisibility(View.VISIBLE);
        view.setCellButtonText(StringUtils.getString(R.string.icon_retry));
        updateTimeLeft();
        checkHighScore();
    }

    private void restartGame() {
        view.hideMenu();
        checkHighScore();
        startIntro();
    }

    private void resumeGame() {
        setGameState(GameStateSingleton.GameState.RUNNING);
        timer.resume();
        view.hideMenu();
    }

    private void startIntro() {
        setGameState(GameStateSingleton.GameState.INTRO);
        view.setCellButtonVisibility(View.VISIBLE);
        new CountDownTimer((long) (INTRO_COUNTDOWN * CommonLib.MS_IN_SEC / INTRO_SPEED), (long) (CommonLib.MS_IN_SEC / INTRO_SPEED)) {
            @Override
            public void onTick(long timeLeft) {
                view.setCellButtonText(String.valueOf(1 + (int) (timeLeft * INTRO_SPEED / CommonLib.MS_IN_SEC)));
            }

            @Override
            public void onFinish() {
                startGame();
            }
        }.start();
    }

    private void checkHighScore() {
        if (getGameState() == GameStateSingleton.GameState.PAUSED)
            return;

        if (score > highScore) {
            highScore = score;
            view.updateHighScore(highScore);
            DbWrapper.getInstance().save(CommonLib.Keys.HIGH_SCORE, highScore).close();
            UiUtils.showToast(StringUtils.getString(R.string.new_high_score));
        } else {
            UiUtils.showToast(StringUtils.getString(R.string.game_over));
        }
    }

    private void reset() {
        timer.stop();
        timer.setTimeLeft(GAME_START_TIME);
        updateTimeLeft();
        view.updateScore(score = 0);
        view.updateHighScore(highScore);
        view.resetLevel();
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
    public void cellClicked(int childCount, int maxCount, int position) {
        if (childCount >= maxCount) {
            addBonusTime();
            view.updateScore(++score);
            view.increaseLevel();
        } else {
            deductPenaltyTime();
        }
    }

    private void addBonusTime() {
        timer.addTime(TIME_BONUS);
    }

    private void deductPenaltyTime() {
        timer.deductTime(TIME_PENALTY);
    }

    @Override
    public void buttonClicked(FragmentPlay.ButtonId buttonId) {
        switch (buttonId) {
            case PLAY:
                if (getGameState() == GameStateSingleton.GameState.MENU) {
                    view.showGame();
                    startIntro();
                    view.hideMenu();
                } else if (getGameState() == GameStateSingleton.GameState.PAUSED) {
                    resumeGame();
                }
                break;
            case RESTART:
                restartGame();
            case CELL:
                if (getGameState() == GameStateSingleton.GameState.OVER)
                    startGame();
                break;
            case MENU_BG:
                if (getGameState() == GameStateSingleton.GameState.PAUSED) {
                    resumeGame();
                }
                break;
        }
    }

    private GameStateSingleton.GameState getGameState() {
        return GameStateSingleton.getInstance().getGameState();
    }

    private void setGameState(GameStateSingleton.GameState gameState) {
        GameStateSingleton.getInstance().setGameState(gameState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getGameState() == GameStateSingleton.GameState.PAUSED) {
            pauseGame();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getGameState() == GameStateSingleton.GameState.RUNNING) {
            setGameState(GameStateSingleton.GameState.PAUSED);
            timer.pause();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CommonLib.Keys.SCORE, score);
        outState.putLong(CommonLib.Keys.TIME_LEFT, timer.getTimeLeft());
    }

    @Subscribe
    public void onEvent(BackpressEvent backpressEvent) {
        switch (getGameState()) {
            case RUNNING:
                pauseGame();
                break;
            case OVER:
                setGameState(GameStateSingleton.GameState.MENU);
                view.showMenu();
                break;
        }
    }

    private void pauseGame() {
        setGameState(GameStateSingleton.GameState.PAUSED);
        timer.pause();
        view.setPlayButtonText(StringUtils.getString(R.string.resume));
        view.showMenu();
    }
}
