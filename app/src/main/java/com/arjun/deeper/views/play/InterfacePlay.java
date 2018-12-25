package com.arjun.deeper.views.play;

import com.arjun.deeper.baseclasses.IBasePresenter;
import com.arjun.deeper.baseclasses.IBaseView;
import com.arjun.deeper.views.Cell;

import java.util.List;

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

        void setPlayButtonText(String text);
    }

    interface IPresenter extends IBasePresenter<IView> {

        void setChildren(List<Cell>children);

        void cellClicked(Cell child);

        void buttonClicked(FragmentPlay.ButtonId buttonId);
    }
}
