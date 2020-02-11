package com.arjun.deeper.interfaces;

import com.arjun.deeper.views.customviews.Cell;

public interface GameGridCallback {

    void cellClicked(Cell child, int maxCount, int position);

    void cellButtonClicked();
}
