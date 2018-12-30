package com.arjun.deeper.views.play;

import com.arjun.deeper.baseclasses.IBasePresenter;
import com.arjun.deeper.baseclasses.IBaseView;

public interface InterfacePlay {

    interface IView extends IBaseView {

        void setTimeLeft(String timeLeftString);

        void updateScore(int score);

        void updateHighScore(int score);

        void setCellButtonVisibility(int visibility);

        void setCellButtonText(String text);

        void showGame();

        void showMenu();

        void hideMenu();

        void showRestartButton();

        void hideRestartButton();

        void setPlayButtonText(String text);

        void increaseLevel();

        void resetLevel();
    }

    interface IPresenter extends IBasePresenter<IView> {

        void cellClicked(int childCount, int maxCount, int position);

        void buttonClicked(FragmentPlay.ButtonId buttonId);
    }
}
