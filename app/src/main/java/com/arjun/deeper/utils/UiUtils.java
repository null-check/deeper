package com.arjun.deeper.utils;

import android.support.annotation.ColorRes;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.arjun.deeper.DeeperApplication;

public class UiUtils {

    public static final float DEFAULT_CORNER_RADIUS = UiUtils.convertDpToPx(3);

    private static final DisplayMetrics metrics = DeeperApplication.getContext().getResources().getDisplayMetrics();

    public static int getScreenWidth() {
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        return metrics.heightPixels;
    }

    public static float convertDpToPx(float dp) {
        return dp * DeeperApplication.getContext().getResources().getDisplayMetrics().density;
    }

    public static void showToast(String text) {
        Toast.makeText(DeeperApplication.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static int getColor(@ColorRes int color) {
        return DeeperApplication.getContext().getResources().getColor(color);
    }
}
