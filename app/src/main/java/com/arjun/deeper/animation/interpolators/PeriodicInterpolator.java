package com.arjun.deeper.animation.interpolators;

import android.view.animation.Interpolator;

/**
 * An interpolator which reverses and repeats ParabolicInterpolator
 * for the amount of times specified in repeatCount
 */

public class PeriodicInterpolator implements Interpolator {

    private final int DEFAULT_REPEAT_COUNT = 3;

    private int repeatCount;
    private boolean positiveOnly;

    public PeriodicInterpolator() {
        repeatCount = DEFAULT_REPEAT_COUNT;
        positiveOnly = false;
    }

    public PeriodicInterpolator(int repeatCount, boolean positiveOnly) {
        this.repeatCount = repeatCount;
        this.positiveOnly = positiveOnly;
    }

    @Override
    public float getInterpolation(float interpolatedTime) {

        float sectionWidth = 1 / ((float) repeatCount * 2);
        float sectionCount = (float) Math.floor(interpolatedTime / sectionWidth);
        interpolatedTime = (interpolatedTime - sectionCount * sectionWidth) * repeatCount * 2;
        int inverter = 1;
        if (!positiveOnly) {
            inverter = sectionCount % 2 == 0 ? 1 : -1;
        }

        return 4 * (interpolatedTime - interpolatedTime * interpolatedTime) * inverter;
    }
}
