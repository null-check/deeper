package com.arjun.deeper.views.play;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import com.arjun.deeper.R;
import com.arjun.deeper.baseclasses.BasePresenter;
import com.arjun.deeper.events.BackpressEvent;
import com.arjun.deeper.interfaces.CallbackDialogGameOver;
import com.arjun.deeper.singletons.GameStateSingleton;
import com.arjun.deeper.sounds.SoundManager;
import com.arjun.deeper.utils.CommonLib;
import com.arjun.deeper.utils.DbWrapper;
import com.arjun.deeper.utils.StringUtils;
import com.arjun.deeper.utils.Timer;
import com.arjun.deeper.utils.UiUtils;
import com.arjun.deeper.views.customviews.Cell;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PresenterPlay extends BasePresenter<InterfacePlay.IView> implements InterfacePlay.IPresenter {

    private final float GAME_START_TIME = 10f;
    private final long INTRO_COUNTDOWN = 3;
    private final float INTRO_SPEED = 2f;
    private final float TIME_BONUS_MIN = 0.5f;
    private final float TIME_PENALTY = 0.5f;

    private int[] TUTORIAL_GRID_STEP_1 = new int[] { 3, 4, 9, 5, 7, 1, 4, 2, 6 };
    private int[] TUTORIAL_GRID_STEP_2 = new int[] { 3, 4, 2, 5, 7, 1, 4, 2, 6 };

    private enum TutorialSource {
        NONE,
        FIRST_LAUNCH,
        USER_CLICK,
        GAME_OVER_DIALOG
    }

    private int roundCount;
    private int score;
    private int highScore;
    private float reactionTime;
    private long lastActionTimestamp;
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
            roundCount = bundle.getInt(CommonLib.Keys.ROUND);
            score = bundle.getInt(CommonLib.Keys.SCORE);
            reactionTime = bundle.getFloat(CommonLib.Keys.REACTION_TIME);
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
        view.showQuickButtons();
        reset();
        timer.start(GAME_START_TIME);
    }

    private void endGame() {
        setGameState(GameStateSingleton.GameState.OVER);
        view.hideRestartButton();
        view.setPlayButtonText(StringUtils.getString(R.string.play));
        view.hideQuickButtons();
        updateTimeLeft();
        showGameOverDialog();
    }

    private void showGameOverDialog() {
        if (getGameState() == GameStateSingleton.GameState.PAUSED)
            return;

        Bundle bundle = new Bundle();
        bundle.putInt(CommonLib.Keys.SCORE, score);
        bundle.putInt(CommonLib.Keys.HIGH_SCORE, highScore);
        bundle.putFloat(CommonLib.Keys.REACTION_TIME, roundCount <= 0 ? 0 : reactionTime / (float) roundCount / 1000F);
        bundle.putFloat(CommonLib.Keys.ACCURACY, roundCount <= 0 ? 0 : score * 100 / (float) roundCount);
        CallbackDialogGameOver callbackDialogGameOver = new CallbackDialogGameOver() {
            @Override
            public void retry() {
                restartGame();
            }

            @Override
            public void mainMenu() {
                view.showMenu();
                setGameState(GameStateSingleton.GameState.MENU);
            }

            @Override
            public void tutorial() {
                showTutorial(TutorialSource.GAME_OVER_DIALOG);
            }
        };
        view.showGameOverDialog(bundle, callbackDialogGameOver);

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
            saveHighscore();
            view.submitHighScore(highScore);
            view.fireConfetti();
            SoundManager.playSound(SoundManager.Sound.APPLAUSE);
        }
    }

    private void saveHighscore() {
        view.updateHighScore(highScore);
        DbWrapper.getInstance().save(CommonLib.Keys.HIGH_SCORE, highScore).close();
    }

    private void reset() {
        timer.stop();
        timer.setTimeLeft(GAME_START_TIME);
        updateTimeLeft();
        roundCount = 0;
        view.updateScore(score = 0);
        view.updateHighScore(highScore);
        reactionTime = 0;
        lastActionTimestamp = System.currentTimeMillis();
        view.resetLevel();
        view.resetProgressBarCurves();
        tutorialStep = 0;
    }

    private void updateTimeLeft() {
        long timeLeftMs = timer.getTimeLeft();
        float timeLeftDecimal = (timeLeftMs / 100) / 10F;
        String timeLeftString = String.valueOf(timeLeftDecimal);
        if (timeLeftString.length() == 1) {
            timeLeftString += ".0";
        }
        view.setTimeLeft(timeLeftString);
        view.setProgress(Math.min(timeLeftMs / 100F, 100F));
        if (timeLeftMs > 10000F) {
            view.setProgressBarColor(UiUtils.getColor(R.color.green_medium));
        } else {
            view.setProgressBarColor(UiUtils.getColor(R.color.nice_red));
        }
    }

    @Override
    public void cellClicked(Cell child, int maxCount, int position) {
        child.clearAnimations();
        boolean correctChoice = child.getChildCellCount() >= maxCount;
        if (correctChoice)
            child.correctOptionFeedback();
        else
            child.wrongOptionFeedback();

        roundCount++;
        if (getGameState() == GameStateSingleton.GameState.RUNNING) {
            long currentTimestamp = System.currentTimeMillis();
            reactionTime += currentTimestamp - lastActionTimestamp;
            lastActionTimestamp = currentTimestamp;
            if (correctChoice) {
                onCorrectChoice();
            } else {
                onWrongChoice();
            }
        } else if (getGameState() == GameStateSingleton.GameState.TUTORIAL) {
            progressTutorial(correctChoice);
        }
    }

    private void onCorrectChoice() {
        addBonusTime();
        view.updateScore(++score);
        view.increaseLevel();
        if (highScore > 0 && score == highScore + 1) {
            SoundManager.playSound(SoundManager.Sound.WHOO);
            view.fireConfettiLight();
        }
        SoundManager.playSound(SoundManager.Sound.RIGHT_CLICK);
    }

    private void onWrongChoice() {
        deductPenaltyTime();
        SoundManager.playSound(SoundManager.Sound.WRONG_CLICK);
    }

    private void addBonusTime() {
        timer.addTime(Math.max(TIME_BONUS_MIN, 2f - ((float) score / 100)));
    }

    private void deductPenaltyTime() {
        timer.deductTime(TIME_PENALTY);
    }

    @Override
    public void buttonClicked(FragmentPlay.ButtonId buttonId) {
        SoundManager.playSound(SoundManager.Sound.RIGHT_CLICK); // TODO Avoid in menu bg click (low priority)
        switch (buttonId) {
            case PLAY:
                if (getGameState() == GameStateSingleton.GameState.MENU) {
                    if (DbWrapper.getBoolean(CommonLib.Keys.TUTORIAL_SHOWN, false)) {
                        view.showGame();
                        startIntro();
                        view.setHintVisibility(View.INVISIBLE);
                        view.hideMenu();
                    } else {
                        showTutorial(TutorialSource.FIRST_LAUNCH);
                    }
                } else if (getGameState() == GameStateSingleton.GameState.PAUSED) {
                    resumeGame();
                }
                break;
            case PAUSE:
                pauseGame();
                break;
            case RESTART:
                timer.pause();
                restartGame();
                break;
            case TUTORIAL:
                showTutorial(TutorialSource.USER_CLICK);
                break;
            case SCOREBOARD:
                openLeaderboards();
                break;
            case SHARE:
                shareOnSocialMedia();
                break;
            case SIGN_IN:
                startSignIn();
                break;
            case CELL:
                if (getGameState() == GameStateSingleton.GameState.OVER) {
                    startGame();
                } else if (getGameState() == GameStateSingleton.GameState.TUTORIAL) {
                    view.setHintVisibility(View.INVISIBLE);
                    tutorialSource = TutorialSource.NONE;
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

    @Override
    public void onLogin(long score) {
        if (score > highScore) {
            highScore = (int) score;
            saveHighscore();
        } else if (highScore > score) {
            view.submitHighScore(highScore);
        }
    }

    private void startSignIn() {
        view.startSignIn();
    }

    private void openLeaderboards() {
        view.openLeaderboards();
    }

    private void shareOnSocialMedia() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Hey! Try out this exciting game that I found.\n");

//        val isPackageInstalled = CommonLib.isPackageInstalled(packageName, context)
//
//        if (isPackageInstalled) {
//            intent.setPackage(packageName)
//        }
//
//        if (isPackageInstalled && !TextUtils.isEmpty(socialShareData.image)) {
//            loadImage(socialShareData.image!!, object: BitmapCallback {
//                override fun onBitmapLoaded(bitmap:Bitmap) {
//                    intent.type = "image/jpeg"
//                    intent.putExtra(Intent.EXTRA_STREAM, getLocaImageUri(context, bitmap))
//                    context.startActivity(Intent.createChooser(intent, sharerDialogTitle))
//                }
//            })
//        } else {
//            intent.type = "text/plain"
//            context.startActivity(Intent.createChooser(intent, sharerDialogTitle))
//        }
    }

    private void showTutorial(TutorialSource tutorialSource) {
        this.tutorialSource = tutorialSource;
        setGameState(GameStateSingleton.GameState.TUTORIAL);
        reset();
        view.setPlayButtonText(StringUtils.getString(R.string.play));
        view.hideRestartButton();
        view.setCellButtonVisibility(View.GONE);
        view.hideMenu();
        showTutorial(tutorialStep);
        DbWrapper.getInstance().save(CommonLib.Keys.TUTORIAL_SHOWN, true).close();
    }

    private void showTutorial(int step) {
        switch (step) {
            case 0:
                view.setChildren(TUTORIAL_GRID_STEP_1);
                view.highlightCell(2);
                view.setHintVisibility(View.VISIBLE);
                view.setHintTitle(StringUtils.getString(R.string.tutorial));
                view.setHintMessage(StringUtils.getString(R.string.tutorial_step_1));
                break;
            case 1:
                view.setChildren(TUTORIAL_GRID_STEP_2);
                view.highlightCell(4);
                view.setHintTitle(StringUtils.getString(R.string.tutorial_title_2));
                view.setHintMessage(StringUtils.getString(R.string.tutorial_step_2));
                break;
            case 2:
                view.setHintTitle(StringUtils.getString(R.string.tutorial_title_3));
                view.setHintMessage(StringUtils.getString(R.string.tutorial_step_3));
                view.setCellButtonVisibility(View.VISIBLE);
                view.setCellButtonText(StringUtils.getString(R.string.play_caps));
                view.fireConfetti();
                SoundManager.playSound(SoundManager.Sound.APPLAUSE);
                break;
        }
    }

    private void progressTutorial(boolean correctOptionSelected) {
        switch (tutorialStep) {
            case 0:
            case 1:
                if (correctOptionSelected) {
                    tutorialStep++;
                    addBonusTime();
                    view.updateScore(++score);
                    SoundManager.playSound(SoundManager.Sound.RIGHT_CLICK);
                    showTutorial(tutorialStep);
                } else {
                    SoundManager.playSound(SoundManager.Sound.WRONG_CLICK);
                }
                break;
            case 2:
                // Only used for overlay version of the tutorial (Deprecated)
                view.setHintVisibility(View.INVISIBLE);
                tutorialStep = 0;
                if (tutorialSource == TutorialSource.FIRST_LAUNCH || tutorialSource == TutorialSource.GAME_OVER_DIALOG) {
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
        outState.putInt(CommonLib.Keys.ROUND, roundCount);
        outState.putInt(CommonLib.Keys.SCORE, score);
        outState.putFloat(CommonLib.Keys.REACTION_TIME, reactionTime);
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
