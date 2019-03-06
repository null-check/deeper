package com.arjun.deeper.views.play;

import android.os.Bundle;

import com.arjun.deeper.baseclasses.IBasePresenter;
import com.arjun.deeper.baseclasses.IBaseView;
import com.arjun.deeper.interfaces.CallbackDialogGameOver;

public interface InterfacePlay {

    interface IView extends IBaseView {

        void setTimeLeft(String timeLeftString);

        void updateScore(int score);

        void updateHighScore(int score);

        void setProgress(float progress);

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

        void setChildren(int childCounts[]);

        void setShowCount(boolean showCount);

        void setHintVisibility(int visibility);

        void setHintTitle(String title);

        void setHintMessage(String message);

        void startSignIn();

        void openLeaderboards();

        void submitHighScore(int score);

        void fireConfetti();

        void fireConfettiLight();
    }

    interface IPresenter extends IBasePresenter<IView> {

        void cellClicked(int childCount, int maxCount, int position);

        void buttonClicked(FragmentPlay.ButtonId buttonId);
    }
}
