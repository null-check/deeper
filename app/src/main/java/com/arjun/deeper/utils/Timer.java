package com.arjun.deeper.utils;

import android.os.CountDownTimer;

public class Timer {

    private final int COUNT_DOWN_INTERVAL = 10;

    private CountDownTimer countDownTimer;
    private long currentTimeLeft;

    private TimerCallback timerCallback;

    public Timer(TimerCallback timerCallback) {
        this.timerCallback = timerCallback;
    }

    public interface TimerCallback {
        void onTick(long timeLeft);

        void onFinish();
    }

    public void start(float timeSeconds) {
        start((long) (timeSeconds * CommonLib.MS_IN_SEC));
    }

    public void start(long timeMilliseconds) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (timeMilliseconds > 0) {
            currentTimeLeft = timeMilliseconds;
            countDownTimer = new CountDownTimer(timeMilliseconds, COUNT_DOWN_INTERVAL) {
                @Override
                public void onTick(long timeLeft) {
                    currentTimeLeft = timeLeft;
                    timerCallback.onTick(timeLeft);
                }

                @Override
                public void onFinish() {
                    timerCallback.onFinish();
                }
            }.start();
        } else {
            currentTimeLeft = 0;
            timerCallback.onFinish();
        }
    }

    public void stop() {
        pause();
        currentTimeLeft = 0;
    }

    public void pause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void resume() {
        start(getTimeLeft());
    }

    public void setTimeLeft(float timeLeftSeconds) {
        setTimeLeft((long) (timeLeftSeconds * CommonLib.MS_IN_SEC));
    }

    public void setTimeLeft(long timeLeftMilliseconds) {
        this.currentTimeLeft = timeLeftMilliseconds;
    }

    public void addTime(float amount) {
        start(getTimeLeft() + (long) amount * CommonLib.MS_IN_SEC);
    }

    public void deductTime(float amount) {
        start(getTimeLeft() - (long) amount * CommonLib.MS_IN_SEC);
    }

    public long getTimeLeft() {
        return currentTimeLeft;
    }
}
