package com.arjun.deeper.views.menu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arjun.deeper.R;
import com.arjun.deeper.interfaces.FragmentMenuCallback;
import com.arjun.deeper.views.MenuButtonView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentMenu extends Fragment implements InterfaceMenu.IView {

    @BindView(R.id.play_button)
    protected MenuButtonView playButton;

    @BindView(R.id.tutorial_button)
    protected MenuButtonView tutorialButton;

    @BindView(R.id.scoreboard_button)
    protected MenuButtonView scoreboardButton;

    private PresenterMenu presenterMenu;

    public FragmentMenu() {
        presenterMenu = new PresenterMenu(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, rootView);
        setupView();
        return rootView;
    }

    private void setupView() {
        setupClickListeners();
    }

    private void setupClickListeners() {
        playButton.setOnClickListener(v -> presenterMenu.playButtonClicked());
    }

    public void setFragmentMenuCallback(FragmentMenuCallback fragmentMenuCallback) {
        presenterMenu.setFragmentMenuCallback(fragmentMenuCallback);
    }
}
