package com.arjun.deeper.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

public class AnimationUtils {

    public static final int D_300 = 300;
    public static final int D_1500 = 1500;
    public static final DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(1.5f);

    public static void popInView(View view) {
        view.clearAnimation();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                view.setScaleX(interpolatedTime);
                view.setScaleY(interpolatedTime);
            }

            @Override
            public boolean willChangeBounds() {
                return false;
            }
        };
        animation.setDuration(D_300);
        animation.setInterpolator(decelerateInterpolator);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(animation);
    }

    public static void popOutView(View view, int endVisibility) {
        view.clearAnimation();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                view.setScaleX(1f - interpolatedTime);
                view.setScaleY(1f - interpolatedTime);
            }

            @Override
            public boolean willChangeBounds() {
                return false;
            }
        };
        animation.setDuration(D_300);
        animation.setInterpolator(decelerateInterpolator);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(endVisibility);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(animation);
    }

    public static Animation expandWidth(final View view, int startWidth, final int endWidth, int duration, Interpolator interpolator, Animation.AnimationListener listener) {

        // Older versions of android (pre API 21) cancel animations for views with a width of 0.
        view.getLayoutParams().width = startWidth > 0 ? startWidth : 1;
        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                view.getLayoutParams().width = (int) (endWidth * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        animation.setInterpolator(interpolator != null ? interpolator : decelerateInterpolator);
        if (listener != null) animation.setAnimationListener(listener);
        view.startAnimation(animation);
        return animation;
    }

    public static void fadeColors(final View view, int startColor, final int endColor) {
        fadeColors(view, startColor, endColor, D_300, new AccelerateInterpolator(), null);
    }

    public static Animation fadeColors(final View view, int startColor, final int endColor, int duration, Interpolator interpolator, Animation.AnimationListener listener) {

        view.setBackgroundColor(startColor);
        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                view.setBackgroundColor(UiUtils.blendColors(startColor, endColor, interpolatedTime));
            }

            @Override
            public boolean willChangeBounds() {
                return false;
            }
        };

        animation.setDuration(duration);
        animation.setInterpolator(interpolator != null ? interpolator : decelerateInterpolator);
        if (listener != null) animation.setAnimationListener(listener);
        view.startAnimation(animation);
        return animation;
    }
}
