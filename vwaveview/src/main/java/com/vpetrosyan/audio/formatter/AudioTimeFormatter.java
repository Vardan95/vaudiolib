package com.vpetrosyan.audio.formatter;

/**
 * Created by varan on 2/24/18.
 */
public interface AudioTimeFormatter {
    String getFormatStringSample();
    String formatTime(long timeInMillis);
}
