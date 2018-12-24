package com.arjun.deeper.views.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arjun.deeper.R;
import com.arjun.deeper.views.Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.LinearLayout;

public class FragmentHome extends Fragment implements InterfaceHome.IActivity {

    @BindView(R.id.root_view)
    protected ViewGroup rootView;

    @BindView(R.id.time_left)
    protected TextView timeLeft;

    @BindView(R.id.score_value)
    protected TextView scoreTextView;

    @BindView(R.id.high_score_value)
    protected TextView highScoreTextView;

    @BindView(R.id.grid_root)
    protected LinearLayout gridRoot;

    @BindView(R.id.button)
    protected View button;

    @BindView(R.id.cell_1) protected Cell cell1;
    @BindView(R.id.cell_2) protected Cell cell2;
    @BindView(R.id.cell_3) protected Cell cell3;
    @BindView(R.id.cell_4) protected Cell cell4;
    @BindView(R.id.cell_5) protected Cell cell5;
    @BindView(R.id.cell_6) protected Cell cell6;
    @BindView(R.id.cell_7) protected Cell cell7;
    @BindView(R.id.cell_8) protected Cell cell8;
    @BindView(R.id.cell_9) protected Cell cell9;

    private PresenterHome presenterHome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        presenterHome = new PresenterHome(this);
        setupView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupView() {
        List<Cell> children = new ArrayList(Arrays.asList(cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9));
        presenterHome.setChildren(children);
        setClickListeners(children);
    }

    private void setClickListeners(List<Cell> children) {
        for (Cell child : children) {
            child.setOnClickListener(view -> {
                if (presenterHome.isRunning()) presenterHome.cellClicked(child);
            });
        }

        button.setOnClickListener(view -> presenterHome.buttonClicked());
    }

    @Override
    public void setTimeLeft(String timeLeftString) {
        timeLeft.setText(timeLeftString);
    }

    @Override
    public void updateScore(int score) {
        scoreTextView.setText(String.valueOf(score));
    }

    @Override
    public void updateHighScore(int score) {
        highScoreTextView.setText(String.valueOf(score));
    }

    @Override
    public void setButtonVisibility(int visibility) {
        button.setVisibility(visibility);
    }
}
