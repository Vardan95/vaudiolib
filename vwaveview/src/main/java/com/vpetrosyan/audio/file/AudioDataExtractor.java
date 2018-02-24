package com.vpetrosyan.audio.file;

import android.content.Context;

/**
 * Created by varan on 2/24/18.
 */

public interface AudioDataExtractor {
    AudioData extractData(String path);
    AudioData extractData(Context context, int resId);
}
