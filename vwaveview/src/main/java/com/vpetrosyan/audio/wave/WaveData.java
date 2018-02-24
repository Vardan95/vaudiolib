package com.vpetrosyan.audio.wave;

/**
 * Created by varan on 2/25/18.
 */

public class WaveData {
    public final short[][] extremes;
    public final int maxValue;

    WaveData(short[][] extremes, int maxValue) {
        this.extremes = extremes;
        this.maxValue = maxValue;
    }
}
