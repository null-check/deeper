package com.arjun.deeper.animation.interpolators;

import android.view.animation.Interpolator;

/**
 * An interpolator that returns a value smoothly transitioning
 * from 0 to 1 and back to 0.
 *
 * Link to graph : https://www.google.co.in/search?q=4*(x-x^2)
 */

public class ParabolicInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float interpolatedTime) {
        return 4 * (interpolatedTime - interpolatedTime * interpolatedTime);
    }
}
