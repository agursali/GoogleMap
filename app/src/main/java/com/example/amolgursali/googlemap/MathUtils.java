package com.example.amolgursali.googlemap;

/**
 * Created by AmolGursali on 7/7/2017.
 */

public class MathUtils
{
    static int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
