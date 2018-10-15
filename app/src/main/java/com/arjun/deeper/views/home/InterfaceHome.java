package com.arjun.deeper.views.home;

import com.arjun.deeper.baseclasses.IBasePresenter;
import com.arjun.deeper.baseclasses.IBaseView;
import com.arjun.deeper.views.Cell;

public interface InterfaceHome {

    interface IActivity extends IBaseView {

        void hideStatusBar();

        void setTimeLeft(String timeLeftString);
    }

    interface IPresenter extends IBasePresenter<IActivity> {

        void cellClicked(Cell child);

        void buttonClicked();
    }
}
