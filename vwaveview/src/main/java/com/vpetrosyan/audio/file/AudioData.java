package com.vpetrosyan.audio.file;

import com.vpetrosyan.audio.utils.AudioUtils;

/**
 * Created by varan on 2/24/18.
 */

public class AudioData {
    public final long audioLength;
    public final int sampleRate;
    public final int channels;
    public final short[] samples;
    public final int bits;

    AudioData(short[] samples, int sampleRate, int channels, int bits) {
        this.audioLength = AudioUtils.calculateAudioLength(samples.length, sampleRate, channels);
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.samples = samples;
        this.bits = bits;
    }
}
