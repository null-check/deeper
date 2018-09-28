package com.arjun.deeper.views.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.arjun.deeper.R;
import com.arjun.deeper.views.Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.LinearLayout;

public class ActivityHome extends AppCompatActivity implements InterfaceHome.IActivity {

    @BindView(R.id.root_view)
    protected ViewGroup rootView;

    @BindView(R.id.grid_root)
    protected LinearLayout gridRoot;

    @BindView(R.id.button)
    protected ViewGroup button;

    @BindView(R.id.cell_1) protected Cell cell1;
    @BindView(R.id.cell_2) protected Cell cell2;
    @BindView(R.id.cell_3) protected Cell cell3;
    @BindView(R.id.cell_4) protected Cell cell4;
    @BindView(R.id.cell_5) protected Cell cell5;
    @BindView(R.id.cell_6) protected Cell cell6;
    @BindView(R.id.cell_7) protected Cell cell7;
    @BindView(R.id.cell_8) protected Cell cell8;
    @BindView(R.id.cell_9) protected Cell cell9;

    private List<Cell> children;

    private PresenterHome presenterHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateView();
        setupView();
        presenterHome = new PresenterHome(this);
    }

    private void inflateView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    private void setupView() {
        children = new ArrayList<>(Arrays.asList(cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9));
        setClickListeners();
    }

    private void setClickListeners() {
        for (Cell child : children) {
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenterHome.cellClicked(child);
                }
            });
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenterHome.buttonClicked();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenterHome.onResume();
    }

    @SuppressLint("InlinedApi")
    @Override
    public void hideStatusBar() {

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
