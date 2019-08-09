package com.arjun.deeper.views.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.arjun.deeper.BuildConfig;
import com.arjun.deeper.DeeperApplication;
import com.arjun.deeper.R;
import com.arjun.deeper.baseclasses.BasePresenter;
import com.arjun.deeper.events.BackpressEvent;
import com.arjun.deeper.interfaces.CallbackDialogGameOver;
import com.arjun.deeper.singletons.GameStateSingleton;
import com.arjun.deeper.utils.CommonLib;
import com.arjun.deeper.utils.DbWrapper;
import com.arjun.deeper.utils.StringUtils;
import com.arjun.deeper.utils.Timer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


import carbon.view.View;

public class PresenterPlay extends BasePresenter<InterfacePlay.IView> implements InterfacePlay.IPresenter {

    private final float GAME_START_TIME = 10f;
    private final long INTRO_COUNTDOWN = 3;
    private final float INTRO_SPEED = 2f;
    private final float TIME_BONUS_MIN = 0.5f;
    private final float TIME_PENALTY = 0.5f;

    private int[] TUTORIAL_GRID_STEP_1 = new int[]{3, 4, 9, 5, 7, 1, 4, 2, 6};

    private enum TutorialSource {
        NONE,
        FIRST_LAUNCH,
        USER_CLICK
    }

    private int round;
    private int score;
    private int highScore;
    private float reactionTime;
    private long lastActionTimestamp;
    private TutorialSource tutorialSource = TutorialSource.NONE;
    private int tutorialStep = 0;

    private Timer timer;
    private SoundPool soundPool;
    private AsyncTask asyncSoundLoader;

    // Sounds
    private int applause;
    private int whooo;
    private int crickets;
    private int laughing;
    private int sarcastic;
    private int menuClick;
    private int wrongClick;

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
        if (BuildConfig.DEBUG)
            view.setShowCount(true);
    }

    @Override
    public void unpackBundle(Bundle bundle) {
        super.unpackBundle(bundle);
        if (bundle != null) {
            timer.setTimeLeft(bundle.getLong(CommonLib.Keys.TIME_LEFT));
            round = bundle.getInt(CommonLib.Keys.ROUND);
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
        reset();
        timer.start(GAME_START_TIME);
    }

    private void endGame() {
        setGameState(GameStateSingleton.GameState.OVER);
        view.hideRestartButton();
        view.setPlayButtonText(StringUtils.getString(R.string.play));
        updateTimeLeft();
        showGameOverDialog();
    }

    private void showGameOverDialog() {
        if (getGameState() == GameStateSingleton.GameState.PAUSED)
            return;

        Bundle bundle = new Bundle();
        bundle.putInt(CommonLib.Keys.SCORE, score);
        bundle.putInt(CommonLib.Keys.HIGH_SCORE, highScore);
        bundle.putFloat(CommonLib.Keys.REACTION_TIME, round <= 0 ? 0 : reactionTime / (float) round / 1000F);
        bundle.putFloat(CommonLib.Keys.ACCURACY, round <= 0 ? 0 : score * 100 / (float) round);
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
                showTutorial(TutorialSource.FIRST_LAUNCH);
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
            playSound(applause);
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
        round = 0;
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
        round++;
        if (getGameState() == GameStateSingleton.GameState.RUNNING) {
            long currentTimestamp = System.currentTimeMillis();
            reactionTime += currentTimestamp - lastActionTimestamp;
            lastActionTimestamp = currentTimestamp;
            if (childCount >= maxCount) {
                onCorrectChoice();
            } else {
                onWrongChoice();
            }
        } else if (getGameState() == GameStateSingleton.GameState.TUTORIAL) {
            progressTutorial(childCount >= maxCount);
        }
    }

    private void onCorrectChoice() {
        addBonusTime();
        view.updateScore(++score);
        view.increaseLevel();
        if (highScore > 0 && score == highScore + 1) {
            playSound(whooo);
            view.fireConfettiLight();
        }
        playSound(menuClick);
    }

    private void onWrongChoice() {
        deductPenaltyTime();
        playSound(wrongClick);
    }

    private void addBonusTime() {
        timer.addTime(Math.max(TIME_BONUS_MIN, 2f - ((float) score / 100)));
    }

    private void deductPenaltyTime() {
        timer.deductTime(TIME_PENALTY);
    }

    @Override
    public void buttonClicked(FragmentPlay.ButtonId buttonId) {
        playSound(menuClick); // TODO Avoid in menu bg click
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
        loadSounds();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        releaseSounds();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CommonLib.Keys.ROUND, round);
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

    @SuppressLint("StaticFieldLeak")
    private void loadSounds() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        asyncSoundLoader = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Context context = DeeperApplication.getContext();
                applause = soundPool.load(context, R.raw.applause, 1);
                whooo = soundPool.load(context, R.raw.whooo, 1);
//                crickets = soundPool.load(context, R.raw.crickets, 1);
//                laughing = soundPool.load(context, R.raw.laughing, 1);
//                sarcastic = soundPool.load(context, R.raw.sarcastic_yay, 1);
                menuClick = soundPool.load(context, R.raw.menu_click, 1);
                wrongClick = soundPool.load(context, R.raw.wrong_click, 1);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                asyncSoundLoader = null;
            }
        }.execute();
    }

    private void releaseSounds() {
        if (asyncSoundLoader != null) {
            asyncSoundLoader.cancel(true);
            asyncSoundLoader = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    private void playSound(int soundId) {
        soundPool.play(soundId, 1, 1, 1, 0, 1);
    }
}
