package com.arjun.deeper.views.gameover;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.arjun.deeper.R;
import com.arjun.deeper.interfaces.CallbackDialogGameOver;
import com.arjun.deeper.utils.CommonLib;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogGameOver extends Dialog {

    @BindView(R.id.title)
    protected TextView title;

    @BindView(R.id.subtitle)
    protected TextView subtitle;

    @BindView(R.id.game_details)
    protected ViewGroup gameDetails;

    @BindView(R.id.score_text)
    protected TextView scoreText;

    @BindView(R.id.score_value)
    protected TextView scoreValue;

    @BindView(R.id.highscore_text)
    protected TextView highscoreText;

    @BindView(R.id.highscore_value)
    protected TextView highscoreValue;

    @BindView(R.id.reaction_time_value)
    protected TextView reactionTimeValue;

    @BindView(R.id.accuracy_value)
    protected TextView accuracyValue;

    @BindView(R.id.button_menu)
    protected ViewGroup buttonMenu;

    @BindView(R.id.button_tutorial)
    protected ViewGroup buttonTutorial;

    @BindView(R.id.button_retry)
    protected ViewGroup buttonRetry;

    private int score;
    private int highscore;
    private float reactionTime;
    private float accuracy;

    private CallbackDialogGameOver callbackDialogGameOver;

    public DialogGameOver(@NonNull Context context, Bundle bundle, CallbackDialogGameOver callbackDialogGameOver) {
        super(context);
        unpackBundle(bundle);
        setActionCallback(callbackDialogGameOver);
    }

    private void unpackBundle(Bundle bundle) {
        score = bundle.getInt(CommonLib.Keys.SCORE);
        highscore = bundle.getInt(CommonLib.Keys.HIGH_SCORE);
        reactionTime = bundle.getFloat(CommonLib.Keys.REACTION_TIME);
        accuracy = bundle.getFloat(CommonLib.Keys.ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    private void initializeView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_gameover);
        ButterKnife.bind(this);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setupView();
    }

    private void setupView() {
        if (score == 0) {
            title.setText(R.string.noob_result_title);
            subtitle.setText(R.string.noob_result_subtitle);
            gameDetails.setVisibility(View.GONE);
            scoreText.setVisibility(View.GONE);
            scoreValue.setVisibility(View.GONE);
            buttonTutorial.setVisibility(View.VISIBLE);
        } else if (score > highscore) {
            title.setText(R.string.success_result_title);
            subtitle.setText(R.string.success_result_subtitle);
        } else {
            if (highscore - score < 3) {
                title.setText(R.string.almost_success_result_title);
                subtitle.setText(R.string.almost_success_result_subtitle);
            } else if (score < 5) {
                title.setText(R.string.bad_result_title);
                subtitle.setText(R.string.bad_result_subtitle);
            } else {
                title.setText(R.string.normal_result_title);
                subtitle.setText(R.string.normal_result_subtitle);
            }
        }

        scoreValue.setText(String.valueOf(score));
        highscoreValue.setText(String.valueOf(highscore));
        reactionTimeValue.setText(Math.round(reactionTime * 100) / 100F + "s");
        accuracyValue.setText(Math.round(accuracy * 100) / 100F + "%");

        buttonMenu.setOnClickListener(view -> {
            dismiss();
            callbackDialogGameOver.mainMenu();
        });
        buttonTutorial.setOnClickListener(view -> {
            dismiss();
            callbackDialogGameOver.tutorial();
        });
        buttonRetry.setOnClickListener(view -> {
            dismiss();
            callbackDialogGameOver.retry();
        });
        setOnCancelListener(dialog -> callbackDialogGameOver.retry());
    }

    public void setActionCallback(CallbackDialogGameOver callbackDialogGameOver) {
        this.callbackDialogGameOver = callbackDialogGameOver;
    }
}
