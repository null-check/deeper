package com.arjun.deeper.views.menu;

import com.arjun.deeper.baseclasses.BasePresenter;
import com.arjun.deeper.interfaces.FragmentMenuCallback;

public class PresenterMenu extends BasePresenter<InterfaceMenu.IView> implements InterfaceMenu.IPresenter {

    private FragmentMenuCallback fragmentMenuCallback;

    public PresenterMenu(InterfaceMenu.IView view) {
        super(view);
    }

    @Override
    public void playButtonClicked() {
        if (fragmentMenuCallback != null) fragmentMenuCallback.playButtonClicked();
    }

    @Override
    public void setFragmentMenuCallback(FragmentMenuCallback fragmentMenuCallback) {
        this.fragmentMenuCallback = fragmentMenuCallback;
    }
}
