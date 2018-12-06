package com.arjun.deeper.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

public class AnimationUtils {

    public static final int D_300 = 300;
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
}
