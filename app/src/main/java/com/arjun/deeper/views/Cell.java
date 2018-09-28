package com.arjun.deeper.views;

import android.content.Context;
import android.util.AttributeSet;

import com.arjun.deeper.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.FrameLayout;
import carbon.widget.LinearLayout;

public class Cell extends LinearLayout {

    @BindView(R.id.child_1) protected FrameLayout child1;
    @BindView(R.id.child_2) protected FrameLayout child2;
    @BindView(R.id.child_3) protected FrameLayout child3;
    @BindView(R.id.child_4) protected FrameLayout child4;
    @BindView(R.id.child_5) protected FrameLayout child5;
    @BindView(R.id.child_6) protected FrameLayout child6;
    @BindView(R.id.child_7) protected FrameLayout child7;
    @BindView(R.id.child_8) protected FrameLayout child8;
    @BindView(R.id.child_9) protected FrameLayout child9;

    public Cell(Context context) {
        super(context);
        init();
    }

    public Cell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Cell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Cell(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflateView();
    }

    private void inflateView() {
        inflate(getContext(), R.layout.view_cell, this);
        ButterKnife.bind(this);
    }
}
