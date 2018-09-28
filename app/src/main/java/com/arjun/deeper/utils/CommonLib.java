package com.arjun.deeper.utils;

import android.util.DisplayMetrics;

import com.arjun.deeper.DeeperApplication;

public class CommonLib {

    // Move to UiUtils
    private static final DisplayMetrics metrics = DeeperApplication.getContext().getResources().getDisplayMetrics();

    public static int getScreenWidth() {
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        return metrics.heightPixels;
    }
}
