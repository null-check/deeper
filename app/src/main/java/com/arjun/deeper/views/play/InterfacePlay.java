package com.arjun.deeper.views.play;

import android.content.Intent;
import android.os.Bundle;

import com.arjun.deeper.baseclasses.IBasePresenter;
import com.arjun.deeper.baseclasses.IBaseView;
import com.arjun.deeper.interfaces.CallbackDialogGameOver;
import com.arjun.deeper.views.customviews.Cell;

public interface InterfacePlay {

    interface IView extends IBaseView {

        void setTimeLeft(String timeLeftString);

        void updateScore(int score);

        void updateHighScore(int score);

        void setProgress(float progress);

        void setProgressBarColor(int progressBarColor);

        void resetProgressBarCurves();

        void setCellButtonVisibility(int visibility);

        void setCellButtonText(String text);

        void showGame();

        void showMenu();

        void hideMenu();

        void showRestartButton();

        void hideRestartButton();

        void setPlayButtonText(String text);

        void showGameOverDialog(Bundle bundle, CallbackDialogGameOver callbackDialogGameOver);

        void increaseLevel();

        void resetLevel();

        void setChildren(int[] childCounts);

        void setShowCount(boolean showCount);

        void highlightCell(int position);

        void setHintVisibility(int visibility);

        void setHintTitle(String title);

        void setHintMessage(String message);

        void showQuickButtons();

        void hideQuickButtons();

        void startSignIn();

        void openLeaderboards();

        void submitHighScore(int score);

        void fireConfetti();

        void fireConfettiLight();

        void correctOptionFeedback();

        void wrongOptionFeedback();

        void shareIntent(Intent intent);
    }

    interface IPresenter extends IBasePresenter<IView> {

        void cellClicked(Cell child, int maxCount, int position);

        void buttonClicked(FragmentPlay.ButtonId buttonId);

        void onLogin(long score);
    }
}
