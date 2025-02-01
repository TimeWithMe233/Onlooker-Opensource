package dev.onlooker.utils.misc;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class RandomUtil {
    private final static Random rand;

    public static int nextInt(int in, int out) {
        int max = Math.max(in, out), min = Math.min(in, out);
        return rand.nextInt(max - min + 1) + min;
    }
    public static int nextDouble(double in, double out) {
        int i = (int) in;
        int i1 = (int) out;
        return rand.nextInt(i - i1) + i1;
    }

    public static long randomDelay(int minDelay, int maxDelay) {
        return nextInt2(minDelay, maxDelay);
    }
    public static int nextInt2(int startInclusive, int endExclusive) {
        return endExclusive - startInclusive <= 0 ? startInclusive : startInclusive + new Random().nextInt(endExclusive - startInclusive);
    }
    public static String randomName() {
        return RandomStringUtils.random(nextInt(14, 8), "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }

    static {
        rand = new Random();
    }
}
