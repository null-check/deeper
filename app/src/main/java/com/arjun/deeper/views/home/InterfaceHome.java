package com.arjun.deeper.views.home;

import com.arjun.deeper.baseclasses.IBasePresenter;
import com.arjun.deeper.baseclasses.IBaseView;
import com.arjun.deeper.views.Cell;

import java.util.List;

public interface InterfaceHome {

    interface IActivity extends IBaseView {

        void hideStatusBar();

        void setTimeLeft(String timeLeftString);

        void updateScore(int score);
    }

    interface IPresenter extends IBasePresenter<IActivity> {

        void setChildren(List<Cell>children);

        void cellClicked(Cell child);

        void buttonClicked();

        boolean isRunning();
    }
}
