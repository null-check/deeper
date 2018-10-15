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

    public void start(long time) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (time > 0) {
            currentTimeLeft = time;
            countDownTimer = new CountDownTimer(time, COUNT_DOWN_INTERVAL) {
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
            timerCallback.onFinish();
        }
    }

    public void stop() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        currentTimeLeft = 0;
    }

    public void setTimeLeft(long timeLeft) {
        this.currentTimeLeft = timeLeft;
    }

    public long getTimeLeft() {
        return currentTimeLeft;
    }
}
