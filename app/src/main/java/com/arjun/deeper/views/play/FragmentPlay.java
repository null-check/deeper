package com.arjun.deeper.views.play;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arjun.deeper.R;
import com.arjun.deeper.interfaces.CallbackDialogGameOver;
import com.arjun.deeper.interfaces.GameGridCallback;
import com.arjun.deeper.singletons.GameStateSingleton;
import com.arjun.deeper.utils.AnimationUtils;
import com.arjun.deeper.utils.StringUtils;
import com.arjun.deeper.utils.UiUtils;
import com.arjun.deeper.views.customviews.Cell;
import com.arjun.deeper.views.customviews.GameGridView;
import com.arjun.deeper.views.customviews.MenuButtonView;
import com.arjun.deeper.views.customviews.ProgressBarView;
import com.arjun.deeper.views.gameover.DialogGameOver;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class FragmentPlay extends Fragment implements InterfacePlay.IView {

    private final int REQUEST_CODE_LEADERBOARD = 1;
    private final int REQUEST_CODE_GOOGLE_SIGN_IN = 2;

    @BindView(R.id.root_view)
    protected ViewGroup rootView;

    @BindView(R.id.game_container)
    protected ViewGroup gameContainer;

    @BindView(R.id.buttons_container)
    protected ViewGroup buttonsContainer;

    @BindView(R.id.restart_button_quick)
    protected ViewGroup quickRestartButton;

    @BindView(R.id.settings_icon)
    protected ImageView settingsIcon;

    @BindView(R.id.pause_button_quick)
    protected ViewGroup quickPauseButton;

    @BindView(R.id.time_left)
    protected TextView timeLeft;

    @BindView(R.id.score_value)
    protected TextView scoreTextView;

    @BindView(R.id.high_score_value)
    protected TextView highScoreTextView;

    @BindView(R.id.progress_bar)
    protected ProgressBarView progressBarView;

    @BindView(R.id.hint_container)
    protected ViewGroup hintContainer;

    @BindView(R.id.hint_title)
    protected TextView hintTitle;

    @BindView(R.id.hint_message)
    protected TextView hintMessage;

    @BindView(R.id.game_grid)
    protected GameGridView gameGridView;

    @BindView(R.id.confetti)
    protected KonfettiView confetti;

    @BindView(R.id.overlay_hint_container)
    protected ViewGroup overlayHintContainer;

    @BindView(R.id.overlay_hint_text)
    protected TextView overlayHintText;

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

    @BindView(R.id.share_button)
    protected MenuButtonView shareButton;

    @BindView(R.id.sign_in_button)
    protected SignInButton signInButton;

    @BindView(R.id.loading_indicator)
    protected ContentLoadingProgressBar loadingIndicator;

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
        PAUSE,
        RESTART,
        TUTORIAL,
        SCOREBOARD,
        SHARE,
        SIGN_IN,
        CELL,
        MENU_BG,
        HINT_OVERLAY
    }

    private PresenterPlay presenterPlay;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterPlay = new PresenterPlay(this);
        setupGoogleSignInClient();
    }

    private void setupGoogleSignInClient() {
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
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
        quickRestartButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.RESTART));
        settingsIcon.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.PAUSE));
        quickPauseButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.PAUSE));
        playButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.PLAY));
        restartButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.RESTART));
        tutorialButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.TUTORIAL));
        scoreboardButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.SCOREBOARD));
        shareButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.SHARE));
        signInButton.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.SIGN_IN));
        menuContainer.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.MENU_BG));
        overlayHintContainer.setOnClickListener(view -> presenterPlay.buttonClicked(ButtonId.HINT_OVERLAY));
        gameGridView.setupGameGridCallback(new GameGridCallback() {
            @Override
            public void cellClicked(Cell child, int maxCount, int position) {
                GameStateSingleton.GameState gameState = GameStateSingleton.getInstance().getGameState();
                if (gameState == GameStateSingleton.GameState.RUNNING
                        || gameState == GameStateSingleton.GameState.TUTORIAL) {
                    presenterPlay.cellClicked(child, maxCount, position);
                }
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
        checkForExistingSignIn();
    }

    private void checkForExistingSignIn() {
        // Check for existing Google Sign In account, if the user is already signed in the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account == null) {
            scoreboardButton.setVisibility(View.GONE);
            startSignIn();
        } else {
            signInButton.setVisibility(View.GONE);
        }
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
    public void setProgress(float progress) {
        progressBarView.setProgress(progress, false);
    }

    @Override
    public void setProgressBarColor(int progressBarColor) {
        progressBarView.setProgressBarColor(progressBarColor);
    }

    @Override
    public void resetProgressBarCurves() {
        progressBarView.resetCurvedCorners();
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
    public void showGameOverDialog(Bundle bundle, CallbackDialogGameOver callbackDialogGameOver) {
        new DialogGameOver(getContext(), bundle, callbackDialogGameOver).show();
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
    public void highlightCell(int position) {
        gameGridView.highlightCell(position);
    }

    @Override
    public void setHintVisibility(int visibility) {
        hintContainer.setVisibility(visibility);
    }

    @Override
    public void setHintTitle(String title) {
        hintTitle.setText(title);
    }

    @Override
    public void setHintMessage(String message) {
        hintMessage.setText(message);
    }

    @Override
    public void showQuickButtons() {
        buttonsContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideQuickButtons() {
        buttonsContainer.setVisibility(View.GONE);
    }

    @Override
    public void startSignIn() {
        loadingIndicator.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    @Override
    public void openLeaderboards() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            Games.getLeaderboardsClient(getContext(), account)
                    .getLeaderboardIntent(StringUtils.getString(R.string.leaderboard_high_scores))
                    .addOnSuccessListener(intent -> startActivityForResult(intent, REQUEST_CODE_LEADERBOARD));
        } else {
            startSignIn();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GOOGLE_SIGN_IN:
                loadingIndicator.hide();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            if (account != null) {
                onSuccessfulSignIn(account);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("GPlayServices", "signInResult:failed code=" + e.getStatusCode());
            UiUtils.showToast("signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void onSuccessfulSignIn(GoogleSignInAccount account) {
        signInButton.setVisibility(View.GONE);
        scoreboardButton.setVisibility(View.VISIBLE);
        Games.getLeaderboardsClient(getContext(), account)
                .loadCurrentPlayerLeaderboardScore(getString(R.string.leaderboard_high_scores), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC)
                .addOnSuccessListener(leaderboardScoreAnnotatedData -> {
                    LeaderboardScore score = leaderboardScoreAnnotatedData.get();
                    if (score != null) {
                        long cloudScore = score.getRawScore();
                        presenterPlay.onLogin(cloudScore);
                    }
                });
    }

    @Override
    public void submitHighScore(int score) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            Games.getLeaderboardsClient(getContext(), account)
                    .submitScore(getString(R.string.leaderboard_high_scores), score);
        }
    }

    @Override
    public void fireConfetti() {
        confetti.build()
                .addColors(UiUtils.getColor(R.color.nice_red), UiUtils.getColor(R.color.green), Color.YELLOW, Color.WHITE)
                .setDirection(30, 150)
                .setSpeed(3f, 10f)
                .setFadeOutEnabled(true)
                .setTimeToLive(1500L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(-50f, confetti.getWidth() + 50f, -50f, -50f)
                .streamFor(150, 3000L);
    }

    @Override
    public void fireConfettiLight() {
        confetti.build()
                .addColors(UiUtils.getColor(R.color.nice_red), UiUtils.getColor(R.color.green), Color.YELLOW, Color.WHITE)
                .setDirection(20, 110)
                .setSpeed(10f, 20f)
                .setFadeOutEnabled(true)
                .setTimeToLive(500L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 3))
                .setPosition(-50f, confetti.getWidth() + 50f, -50f, -50f)
                .streamFor(200, 1000L);
    }

    @Override
    public void correctOptionFeedback() {
        AnimationUtils.fadeColors(rootView, UiUtils.getColor(R.color.blue_dark), UiUtils.getColor(R.color.green));
    }

    @Override
    public void wrongOptionFeedback() {
        AnimationUtils.fadeColors(rootView, UiUtils.getColor(R.color.blue_dark), UiUtils.getColor(R.color.red));
    }

    @Override
    public void shareIntent(Intent intent) {
        if (isAdded())
            getContext().startActivity(Intent.createChooser(intent, "Share with: "));
    }


}
