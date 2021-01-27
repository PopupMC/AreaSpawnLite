package com.popupmc.areaspawnlite.misc;

import java.util.Random;

public class RandomUtil {
    public static int getRandomNumberInRange(int min, int max) {

        // If min > max, flip them around
        if(min > max) {
            int tmpMax = max;
            max = min;
            min = tmpMax;
        }

        // If they're the same, just return one of them
        else if(min == max)
            return min;

        // Randomize range
        return random.nextInt((max - min) + 1) + min;
    }

    public static Random random = new Random();
}
