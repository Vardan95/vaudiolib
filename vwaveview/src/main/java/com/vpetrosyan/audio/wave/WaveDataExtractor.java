package com.vpetrosyan.audio.wave;

import java.util.Arrays;

/**
 * Created by varan on 2/25/18.
 */

public final class WaveDataExtractor {

    public static WaveData getExtremes(short[] data, int sampleSize, int coef) {
        short[][] newData = new short[sampleSize][];
        int groupSize = data.length / sampleSize;

        short absMax = Short.MIN_VALUE;

        for (int i = 0; i < sampleSize; i++) {
            short[] group = Arrays.copyOfRange(data, i * groupSize,
                    Math.min((i + 1) * groupSize, data.length));

            // Fin min & max values
            short min = (short) (Short.MAX_VALUE / coef), max = (short) (Short.MIN_VALUE / coef);
            for (short a : group) {
                min = (short) Math.min(min, a / coef);
                max = (short) Math.max(max, a / coef);
            }

            newData[i] = new short[] { max, min };

            short absTempMax = (short) Math.max(Math.abs(max), Math.abs(max));
            absMax = (short) Math.max(absTempMax, absMax);
        }

        return new WaveData(newData, absMax);
    }

    private WaveDataExtractor() {

    }
}
