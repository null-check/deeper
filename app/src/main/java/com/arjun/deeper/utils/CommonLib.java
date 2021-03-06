package com.arjun.deeper.utils;

import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.arjun.deeper.DeeperApplication;

import java.util.Random;

public class CommonLib {

    public static final long MS_IN_SEC = 1000;

    private static Random random = new Random();

    public class Keys {

        public final static String ROUND = "round";
        public final static String HIGH_SCORE = "high_score";
        public final static String SCORE = "score";
        public final static String TIME_LEFT = "time_left";
        public final static String TUTORIAL_SOURCE = "tutorial_source";
        public final static String TUTORIAL_STEP = "tutorial_step";
        public final static String STAGE = "stage";
        public final static String DIFFICULTY = "difficulty";
        public final static String LEVEL_STEPS_COUNT = "level_steps_count";
        public final static String TUTORIAL_SHOWN = "tutorial_shown";
        public final static String REACTION_TIME = "reaction_time";
        public final static String ACCURACY = "accuracy";
    }

    public static int getRandomIntBetween(int start, int end) {
        return random.nextInt(1 + end - start) + start;
    }

    public static boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    public static long convertSecondsToMs(float seconds) {
        return (long) (seconds * MS_IN_SEC);
    }

    public static boolean isPackageInstalled(String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;

        PackageManager packageManager = DeeperApplication.getContext().getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
