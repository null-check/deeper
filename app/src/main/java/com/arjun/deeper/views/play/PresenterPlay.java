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

    private int[] TUTORIAL_GRID_STEP_1 = new int[]{3, 4, 9, 5, 7, 1, 4, 2, 6};

    private enum TutorialSource {
        NONE,
        FIRST_LAUNCH,
        USER_CLICK
    }

    private int score;
    private int highScore;
    private TutorialSource tutorialSource = TutorialSource.NONE;
    private int tutorialStep = 0;

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
            tutorialSource = TutorialSource.values()[bundle.getInt(CommonLib.Keys.TUTORIAL_SOURCE)];
            tutorialStep = bundle.getInt(CommonLib.Keys.TUTORIAL_STEP);
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
            view.submitHighScore(highScore);
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
        view.resetProgressBarCurves();
        tutorialStep = 0;
    }

    private void updateTimeLeft() {
        long timeLeftMs = timer.getTimeLeft();
        float timeLeftDecimal = (timeLeftMs / 100) / 10F;
        String timeLeftString = String.valueOf(timeLeftDecimal);
        int length = timeLeftString.length();
        switch (length) {
            case 1:
                timeLeftString += ".0";
                break;
        }
        view.setTimeLeft(timeLeftString);
        view.setProgress(Math.min(timeLeftMs / 100F, 100F));
    }

    @Override
    public void cellClicked(int childCount, int maxCount, int position) {
        if (getGameState() == GameStateSingleton.GameState.RUNNING) {
            if (childCount >= maxCount) {
                addBonusTime();
                view.updateScore(++score);
                view.increaseLevel();
            } else {
                deductPenaltyTime();
            }
        } else if (getGameState() == GameStateSingleton.GameState.TUTORIAL) {
            progressTutorial(childCount >= maxCount);
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
                    if (DbWrapper.getBoolean(CommonLib.Keys.TUTORIAL_SHOWN, false)) {
                        view.showGame();
                        startIntro();
                        view.setHintVisibility(View.GONE);
                        view.hideMenu();
                    } else {
                        showTutorial(TutorialSource.FIRST_LAUNCH);
                    }
                } else if (getGameState() == GameStateSingleton.GameState.PAUSED) {
                    resumeGame();
                }
                break;
            case RESTART:
                restartGame();
                break;
            case TUTORIAL:
                showTutorial(TutorialSource.USER_CLICK);
                break;
            case SCOREBOARD:
                openLeaderboards();
                break;
            case SIGN_IN:
                startSignIn();
                break;
            case CELL:
                if (getGameState() == GameStateSingleton.GameState.OVER) {
                    startGame();
                } else if (getGameState() == GameStateSingleton.GameState.TUTORIAL) {
                    view.setHintVisibility(View.GONE);
                    reset();
                    startIntro();
                }
                break;
            case MENU_BG:
                if (getGameState() == GameStateSingleton.GameState.PAUSED) {
                    resumeGame();
                }
                break;
            case HINT_OVERLAY:
                progressTutorial(true);
        }
    }

    private void startSignIn() {
        view.startSignIn();
    }

    private void openLeaderboards() {
        view.openLeaderboards();
    }

    private void showTutorial(TutorialSource tutorialSource) {
        this.tutorialSource = tutorialSource;
        reset();
        setGameState(GameStateSingleton.GameState.TUTORIAL);
        view.setPlayButtonText(StringUtils.getString(R.string.play));
        view.hideRestartButton();
//        view.setCellButtonVisibility(View.GONE);
        view.hideMenu();
        showTutorial(tutorialStep);
        DbWrapper.getInstance().save(CommonLib.Keys.TUTORIAL_SHOWN, true).close();
    }

    private void showTutorial(int step) {
        switch (step) {
            case 0:
//                view.setChildren(TUTORIAL_GRID_STEP_1);
                view.setHintVisibility(View.VISIBLE);
//                view.setHintTitle(StringUtils.getString(R.string.tutorial));
                view.setHintMessage(StringUtils.getString(R.string.tutorial_step_1));
                break;
            case 1:
//                view.setHintTitle(StringUtils.getString(R.string.wonderful));
                view.setHintMessage(StringUtils.getString(R.string.tutorial_step_2));
//                view.setCellButtonVisibility(View.VISIBLE);
//                view.setCellButtonText(StringUtils.getString(R.string.play_caps));
                break;
        }
    }

    private void progressTutorial(boolean correctOptionSelected) {
        switch (tutorialStep) {
            case 0:
                if (correctOptionSelected) {
                    tutorialStep++;
//                    addBonusTime();
//                    view.updateScore(score);
                    showTutorial(tutorialStep);
                }
                break;
            case 1:
                view.setHintVisibility(View.GONE);
                tutorialStep = 0;
                if (tutorialSource == TutorialSource.FIRST_LAUNCH) {
                    startIntro();
                } else if (tutorialSource == TutorialSource.USER_CLICK) {
                    view.showMenu();
                    setGameState(GameStateSingleton.GameState.MENU);
                }
                tutorialSource = TutorialSource.NONE;
                break;
            default:
                // Impossible case.
                reset();
                startIntro();
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
        outState.putInt(CommonLib.Keys.TUTORIAL_SOURCE, tutorialSource.ordinal());
        outState.putInt(CommonLib.Keys.TUTORIAL_STEP, tutorialStep);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setGameState(GameStateSingleton.GameState.MENU);
    }

    @Subscribe
    public void onEvent(BackpressEvent backpressEvent) {
        switch (getGameState()) {
            case RUNNING:
                pauseGame();
                break;
            case TUTORIAL:
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
