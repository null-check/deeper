package com.arjun.deeper.utils;

import android.util.DisplayMetrics;
import android.widget.Toast;

import com.arjun.deeper.DeeperApplication;

public class UiUtils {

    private static final DisplayMetrics metrics = DeeperApplication.getContext().getResources().getDisplayMetrics();

    public static int getScreenWidth() {
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        return metrics.heightPixels;
    }

    public static void showToast(String text) {
        Toast.makeText(DeeperApplication.getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
