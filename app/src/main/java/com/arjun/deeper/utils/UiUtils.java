package com.arjun.deeper.utils;

import android.graphics.Color;
import androidx.annotation.ColorRes;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.arjun.deeper.DeeperApplication;

public class UiUtils {

    public static final float DEFAULT_CORNER_RADIUS = convertDpToPx(3);

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

    public static int blendColors(int color1, int color2, float ratio) {
        final float inverseRatio = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRatio);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRatio);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRatio);
        return Color.rgb((int) r, (int) g, (int) b);
    }
}
