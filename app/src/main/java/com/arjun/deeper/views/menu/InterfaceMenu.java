package com.arjun.deeper.views.menu;

import com.arjun.deeper.baseclasses.IBasePresenter;
import com.arjun.deeper.baseclasses.IBaseView;
import com.arjun.deeper.interfaces.FragmentMenuCallback;

public interface InterfaceMenu {

    interface IView extends IBaseView {
    }

    interface IPresenter extends IBasePresenter<InterfaceMenu.IView> {

        void setFragmentMenuCallback(FragmentMenuCallback fragmentMenuCallback);

        void playButtonClicked();
    }
}
