package com.vpetrosyan.audio.file;

import android.content.Context;
import android.util.Log;

import com.vpetrosyan.audio.utils.ByteUtils;
import com.vpetrosyan.audio.vwaveview.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by varan on 2/24/18.
 */

public class PCMAudioExtractor implements AudioDataExtractor {
    private static final String TAG = PCMAudioExtractor.class.getSimpleName();
    private static final int DEfAULT_PCM_RATE = 44100;

    @Override
    public AudioData extractData(String path) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public AudioData extractData(Context context, int resId) {
        AudioData audioData = null;
        try {
            InputStream is = context.getResources().openRawResource(resId);
            byte[] data = new byte[is.available()];
            is.read(data, 0, data.length);
            is.close();

            audioData = new AudioData(ByteUtils.getPCMData(data), DEfAULT_PCM_RATE,
                    1, 16);

        } catch (IOException e) {
            Log.e(TAG, "Error while reading raw PCM file");
        }

        return audioData;
    }
}
