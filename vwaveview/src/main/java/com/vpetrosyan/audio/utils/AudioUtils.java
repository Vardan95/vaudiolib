package com.vpetrosyan.audio.utils;

import java.util.Arrays;

/**
 * Created by varan on 2/24/18.
 */

public final class AudioUtils {

    private AudioUtils() {

    }

    public static long calculateAudioLength(int samplesCount, int sampleRate, int channelCount) {
        float hz = 1000 / (float) sampleRate;
        long wholeTime = (samplesCount / channelCount);
        return (long) (wholeTime * hz);
    }
}
