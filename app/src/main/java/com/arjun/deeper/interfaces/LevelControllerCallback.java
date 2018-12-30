package com.arjun.deeper.interfaces;

import com.arjun.deeper.views.customviews.Cell;

public interface LevelControllerCallback {

    void setChildren(int[] childCounts);

    void setRotatedCells(boolean flag);

    void setChooseRandomSubcell(boolean flag);

    void setSubcellShape(Cell.SubcellShape subcellShape);

    void setSubcellHideMode(int subcellHideMode);

    void resetAttributes();
}
