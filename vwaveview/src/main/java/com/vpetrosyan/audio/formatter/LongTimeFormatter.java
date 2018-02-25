package com.vpetrosyan.audio.formatter;

import java.util.Locale;

/**
 * Created by varan on 2/24/18.
 */
public class LongTimeFormatter implements AudioTimeFormatter {
    private static final String timeFormatLong = "99:99:99";

    @Override
    public String getFormatStringSample() {
        return timeFormatLong;
    }

    @Override
    public String formatTime(long timeInMillis) {
        int millis = (int) (timeInMillis % 1000d);
        int seconds = (int) ((timeInMillis / 1000d) % 60);
        int minutes = (int) ((timeInMillis / (1000 * 60)) % 60);

        return String.format(Locale.US, "%02d:%02d:%03d", minutes, seconds, millis);
    }
}
