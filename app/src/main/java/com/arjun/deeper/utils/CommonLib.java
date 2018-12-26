package com.arjun.deeper.utils;

import java.util.Random;

public class CommonLib {

    public static final long MS_IN_SEC = 1000;

    private static Random random = new Random();

    public static int getRandomIntBetween(int start, int end) {
        return random.nextInt(1 + end - start) + start;
    }

    public static boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    public class Keys {

        public final static String HIGH_SCORE = "high_score";
    }
}
