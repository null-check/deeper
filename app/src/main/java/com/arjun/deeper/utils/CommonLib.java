package com.arjun.deeper.utils;

import java.util.Random;

public class CommonLib {

    private static Random random = new Random();

    public static int getRandomIntBetween(int start, int end) {
        return random.nextInt(end - start) + start;
    }
}
