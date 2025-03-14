package net.daichang.dclib.utils.math;

import java.security.SecureRandom;
import java.util.Random;

public final class RandomUtils {
    public static final float PI2 = roundToFloat((Math.PI * 2D));
    private static final Random random = new Random();

    public static int nextInt(final int startInclusive, final int endExclusive) {
        if (endExclusive - startInclusive <= 0)
            return startInclusive;

        return startInclusive + new Random().nextInt(endExclusive - startInclusive);
    }

    public static int randomizeInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值比最大值还要大");
        }
        return random.nextInt(max - min + 1) + min;
    }

    public static double nextDouble(final double startInclusive, final double endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0D)
            return startInclusive;

        return startInclusive + ((endInclusive - startInclusive) * Math.random());
    }

    public static float nextFloat(final float startInclusive, final float endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0F)
            return startInclusive;

        return (float) (startInclusive + ((endInclusive - startInclusive) * Math.random()));
    }

    public static String randomNumber(final int length) {
        return random(length, "123456789");
    }

    public static String randomString(final int length) {
        return random(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    public static String random(final int length, final String chars) {
        return random(length, chars.toCharArray());
    }

    public static String random(final int length, final char[] chars) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++)
            stringBuilder.append(chars[new Random().nextInt(chars.length)]);
        return stringBuilder.toString();
    }

    public static float getRandomInRange(float min, float max) {
        SecureRandom random = new SecureRandom();
        return random.nextFloat() * (max - min) + min;
    }

    public static float roundToFloat(double d) {
        return (float) ((double) Math.round(d * 1.0E8D) / 1.0E8D);
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }
}
