package com.arjun.deeper.views.play;

import com.arjun.deeper.baseclasses.IBasePresenter;
import com.arjun.deeper.baseclasses.IBaseView;
import com.arjun.deeper.views.customviews.Cell;

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

        void randomizeViews(int difficulty);

        void setRotatedCells(boolean enable);

        void setChooseRandomSubcell(boolean flag);

        void setSubcellShape(Cell.SubcellShape shape);

        void resetAttributes();
    }

    interface IPresenter extends IBasePresenter<IView> {

        void cellClicked(int childCount, int maxCount, int position);

        void buttonClicked(FragmentPlay.ButtonId buttonId);
    }
}
