package com.vpetrosyan.audio.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by varan on 2/24/18.
 */
public final class SizeUtils {

    public static int convertSpToPixels(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
        return px;
    }

    public static int convertDpToPixels(float dp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
        return px;
    }

    private SizeUtils() {

    }
}
