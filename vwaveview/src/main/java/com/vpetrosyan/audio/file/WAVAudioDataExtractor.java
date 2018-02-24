package com.vpetrosyan.audio.file;

import android.content.Context;
import android.util.Log;

import com.vpetrosyan.audio.utils.AudioUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static com.vpetrosyan.audio.utils.ByteUtils.getPCMData;

/**
 * Created by varan on 2/24/18.
 */

public class WAVAudioDataExtractor implements AudioDataExtractor {
    private static final String TAG = WAVAudioDataExtractor.class.getSimpleName();
    private static final int HEADER_SIZE = 44;

    @Override
    public AudioData extractData(String path) {
        AudioData data = null;
        try {
            data = readWAV(new FileInputStream(path));
        } catch (IOException e) {
            Log.e(TAG, "Exception during WAV file reading", e);
        }
        return data;
    }

    @Override
    public AudioData extractData(Context context, int id) {
        AudioData data = null;
        try {
            data = readWAV(context.getResources().openRawResource(id));
        } catch (IOException e) {
            Log.e(TAG, "Exception during WAV file reading", e);
        }
        return data;
    }

    private AudioData readWAV(InputStream wavStream) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        wavStream.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());

        buffer.rewind();
        buffer.position(buffer.position() + 20);

        int format = buffer.getShort();
        // 1 means Linear PCM
        checkFormat(format == 1, "Unsupported encoding: " + format);

        int channels = buffer.getShort();
        checkFormat(channels == 1 || channels == 2, "Unsupported channels: "
                + channels);

        int rate = buffer.getInt();
        checkFormat(rate <= 48000 && rate >= 11025, "Unsupported rate: " + rate);

        buffer.position(buffer.position() + 6);
        int bits = buffer.getShort();
        checkFormat(bits == 16, "Unsupported bits: " + bits);

        int dataSize = 0;
        while (buffer.getInt() != 0x61746164) { // "data" marker
            int size = buffer.getInt();
            wavStream.skip(size);

            buffer.rewind();
            wavStream.read(buffer.array(), buffer.arrayOffset(), 8);
            buffer.rewind();
        }

        dataSize = buffer.getInt();

        checkFormat(dataSize > 0, "wrong data size: " + dataSize);

        byte[] data = new byte[dataSize];
        wavStream.read(data, 0, data.length);
        short[] pcmData = getPCMData(data);

        AudioData info = new AudioData(pcmData, rate, channels, bits);
        wavStream.close();

        return info;
    }

    private static void checkFormat(boolean assertion, String message) throws RuntimeException {
        if (!assertion) {
            throw new RuntimeException(message);
        }
    }
}
