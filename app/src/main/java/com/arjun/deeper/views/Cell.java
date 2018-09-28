package com.arjun.deeper.views;

import android.content.Context;
import android.util.AttributeSet;

import com.arjun.deeper.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private List<FrameLayout> children;

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
        setupView();
    }

    private void inflateView() {
        inflate(getContext(), R.layout.view_cell, this);
        ButterKnife.bind(this);
    }

    private void setupView() {
        children = new ArrayList<>(Arrays.asList(child1, child2, child3, child4, child5, child6, child7, child8, child9));
        hideAllChildren();
    }

    public void showChildren(int count) {

        if (count >= 9) {
            showAllChildren();
            return;
        } else if (count <=0) {
            hideAllChildren();
            return;
        }

        for (FrameLayout child : children) {
            if (count > 0) {
                child.setVisibility(VISIBLE);
                count--;
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    public void showAllChildren() {
        for (FrameLayout child : children) {
            child.setVisibility(VISIBLE);
        }
    }

    public void hideAllChildren() {
        for (FrameLayout child : children) {
            child.setVisibility(GONE);
        }
    }

    public int getVisibleChildCount() {
        int count = 0;
        for (FrameLayout child : children) {
            if (child.getVisibility() == VISIBLE) {
                count++;
            }
        }
        return count;
    }
}
