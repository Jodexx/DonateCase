package com.jodexindustries.donatecase.api.tools;

public class NumberUtils {

    public static double square(double num) {
        return num * num;
    }

    public static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }
}
