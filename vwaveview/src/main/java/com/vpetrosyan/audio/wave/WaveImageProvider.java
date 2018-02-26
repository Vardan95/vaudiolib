package com.vpetrosyan.audio.wave;

import android.graphics.Bitmap;

/**
 * Created by varan on 2/24/18.
 */
public interface WaveImageProvider {
    Bitmap provideWaveBitmap();

    // TODO(Vardan) implement config mechanism
    int getCalculatedStepLength();
    int getCalculatedStepTime();

    // Returns calculated padding between top and first line
    float getCalculatedVerticalPadding();

    void setSidePadding(int padding);
}
