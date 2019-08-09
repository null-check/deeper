package com.arjun.deeper.views.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.arjun.deeper.R;
import com.arjun.deeper.utils.AnimationUtils;
import com.arjun.deeper.utils.CommonLib;
import com.arjun.deeper.utils.UiUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.FrameLayout;

public class Cell extends FrameLayout {
    private final float SQUARE_CORNER_RADIUS = isInEditMode() ? 9 : UiUtils.DEFAULT_CORNER_RADIUS;
    private final float CIRCLE_CORNER_RADIUS = 1000;

    @BindView(R.id.child_count) protected TextView childCountTv;

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

    private int childCount;
    private int subcellHideMode;
    private boolean chooseRandomSubcell;
    private SubcellShape subcellShape;
    private boolean showCount = false;

    public enum SubcellShape {
        SQUARE,
        CIRCLE,
        RANDOM
    }

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
        resetAttributes();
        setupView();
    }

    private void inflateView() {
        inflate(getContext(), R.layout.view_cell, this);
        ButterKnife.bind(this);
    }

    private void setupView() {
        children = new ArrayList<>(Arrays.asList(child1, child2, child3, child4, child5, child6, child7, child8, child9));
        hideAllChildren();
        if (showCount) childCountTv.setVisibility(VISIBLE);
    }

    public void showChildren(int count) {

        if (showCount) childCountTv.setText(String.valueOf(count));
        childCount = count;
        if (count >= 9) {
            showAllChildren();
            return;
        } else if (count <=0) {
            hideAllChildren();
            return;
        }

        if (chooseRandomSubcell) {
            int visibility;
            if (count < 4) {
                hideAllChildren();
                visibility = VISIBLE;
            } else {
                count = 9 - count;
                showAllChildren();
                visibility = subcellHideMode;
            }

            Set<Integer> selectedAddresses = new HashSet<>();
            while (count > 0) {
                int randomInt = CommonLib.getRandomIntBetween(0, 8);
                if (!selectedAddresses.contains(randomInt)) {
                    selectedAddresses.add(randomInt);
                    FrameLayout child = children.get(randomInt);
                    if (child.getVisibility() != visibility) {
                        if (visibility == VISIBLE) {
                            showSubcell(child);
                        } else {
                            hideSubcell(child);
                        }
                        if (subcellShape == SubcellShape.RANDOM)
                            child.setCornerRadius(CommonLib.getRandomBoolean() ? SQUARE_CORNER_RADIUS : CIRCLE_CORNER_RADIUS);
                        count--;
                    }
                }
            }
        } else {
            for (FrameLayout child : children) {
                if (count > 0) {
                    showSubcell(child);
                    if (subcellShape == SubcellShape.RANDOM) child.setCornerRadius(CommonLib.getRandomBoolean() ? SQUARE_CORNER_RADIUS : CIRCLE_CORNER_RADIUS);
                    count--;
                } else {
                    hideSubcell(child);
                }
            }
        }
    }

    public void showAllChildren() {
        for (FrameLayout child : children) {
            showSubcell(child);
            if (subcellShape == SubcellShape.RANDOM) child.setCornerRadius(CommonLib.getRandomBoolean() ? SQUARE_CORNER_RADIUS : CIRCLE_CORNER_RADIUS);
        }
    }

    public void hideAllChildren() {
        for (FrameLayout child : children) {
            hideSubcell(child);
        }
    }

    public void setSubcellHideMode(int subcellHideMode) {
        this.subcellHideMode = subcellHideMode;
    }

    public void setChooseRandomSubcell(boolean chooseRandomSubcell) {
        this.chooseRandomSubcell = chooseRandomSubcell;
    }

    public void setSubcellShape(SubcellShape subcellShape) {
        this.subcellShape = subcellShape;
        if (subcellShape != SubcellShape.RANDOM) updateSubcells();
    }

    private void updateSubcells() {
        float cornerRadius;
        switch (subcellShape) {
            case SQUARE:
                cornerRadius = SQUARE_CORNER_RADIUS;
                break;
            case CIRCLE:
                cornerRadius = CIRCLE_CORNER_RADIUS;
                break;
            default:
                cornerRadius = UiUtils.DEFAULT_CORNER_RADIUS;
                break;
        }

        for (FrameLayout child : children) {
            child.setCornerRadius(cornerRadius);
        }
    }

    public void resetAttributes() {
        subcellHideMode = INVISIBLE;
        chooseRandomSubcell = false;
        subcellShape = SubcellShape.SQUARE;
        if (children != null) updateSubcells();
    }

    private void showSubcell(FrameLayout subcell) {
        AnimationUtils.popInView(subcell);
    }

    private void hideSubcell(FrameLayout subcell) {
        if (subcell.getVisibility() == VISIBLE) AnimationUtils.popOutView(subcell, subcellHideMode);
    }

    public int getChildCellCount() {
        return childCount;
    }

    public void setShowCount(boolean showCount) {
        this.showCount = showCount;
        if (showCount)
            childCountTv.setVisibility(VISIBLE);
        else
            childCountTv.setVisibility(GONE);
    }
}
