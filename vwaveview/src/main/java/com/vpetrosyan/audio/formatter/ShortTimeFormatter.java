package com.vpetrosyan.audio.formatter;

import java.util.Locale;

/**
 * Created by varan on 2/24/18.
 */
public class ShortTimeFormatter implements AudioTimeFormatter {
    private static final String timeFormatShort = "99:99";

    @Override
    public String getFormatStringSample() {
        return timeFormatShort;
    }

    @Override
    public String formatTime(long timeInMillis) {
        int seconds = (int) ((timeInMillis / 1000d) % 60);
        int minutes = (int) ((timeInMillis / (1000 * 60)) % 60);

        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
}
