package com.arjun.deeper.utils;

import android.os.CountDownTimer;

public class Timer {

    private final int COUNT_DOWN_INTERVAL = 10;

    private CountDownTimer countDownTimer;
    private long currentTimeLeft;
    private boolean running = false;

    private TimerCallback timerCallback;

    public Timer(TimerCallback timerCallback) {
        this.timerCallback = timerCallback;
    }

    public interface TimerCallback {
        void onTick(long timeLeft);

        void onFinish();
    }

    public void start(float timeSeconds) {
        start(CommonLib.convertSecondsToMs(timeSeconds));
    }

    public void start(long timeMilliseconds) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (timeMilliseconds > 0) {
            running = true;
            currentTimeLeft = timeMilliseconds;
            countDownTimer = new CountDownTimer(timeMilliseconds, COUNT_DOWN_INTERVAL) {
                @Override
                public void onTick(long timeLeft) {
                    currentTimeLeft = timeLeft;
                    timerCallback.onTick(timeLeft);
                }

                @Override
                public void onFinish() {
                    running = false;
                    timerCallback.onFinish();
                }
            }.start();
        } else {
            running = false;
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
            running = false;
        }
    }

    public void resume() {
        start(getTimeLeft());
    }

    public void setTimeLeft(float timeLeftSeconds) {
        setTimeLeft(CommonLib.convertSecondsToMs(timeLeftSeconds));
    }

    public void setTimeLeft(long timeLeftMilliseconds) {
        this.currentTimeLeft = timeLeftMilliseconds;
    }

    public void addTime(float amount) {
        if (isRunning())
            start(getTimeLeft() + CommonLib.convertSecondsToMs(amount));
        else
            setTimeLeft(getTimeLeft() + CommonLib.convertSecondsToMs(amount));
    }

    public void deductTime(float amount) {
        if (isRunning())
            start(getTimeLeft() - CommonLib.convertSecondsToMs(amount));
        else
            setTimeLeft(getTimeLeft() - CommonLib.convertSecondsToMs(amount));
    }

    public long getTimeLeft() {
        return currentTimeLeft;
    }

    public boolean isRunning() {
        return running;
    }
}
