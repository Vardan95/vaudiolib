package com.vpetrosyan.audio.wave;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.Log;

import com.vpetrosyan.audio.file.AudioData;
import com.vpetrosyan.audio.formatter.AudioTimeFormatter;
import com.vpetrosyan.audio.utils.AudioUtils;
import com.vpetrosyan.audio.vwaveview.BuildConfig;

/**
 * Created by varan on 2/24/18.
 */

public class NativeWaveImageProvider implements WaveImageProvider {
    private static final String TAG = NativeWaveImageProvider.class.getSimpleName();

    private AudioTimeFormatter formatter;

    private TextPaint textPaint;
    private Paint strokePaint, fillPaint;

    private AudioData audioData;

    private int finalWidth, finalHeight;
    private int waveWidth, waveHeight;

    private int paddingText; // Padding for text
    private int paddingWave; // Padding for wave
    private int paddingSide; // Padding for wave

    private int stepInMillis; // In milliseconds
    private int stepLength;

    private float centerTextY;
    private float startYForTopLine;
    private float startYForBottomLine;
    private float startYForWave;

    private int miniMarkerCount;
    private float miniMarkerDiffCoef;

    private int stepMarkerHeight;
    private int miniMarkerHeight;

    private float lineWidth = 2; // Default 2 Px

    public NativeWaveImageProvider() {
        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(Color.WHITE);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(0.5f);
        strokePaint.setAntiAlias(true);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(Color.WHITE);
    }

    public void setDrawingStyle(float stroke, int strokeColor, int fillColor, int textColor) {
        textPaint.setColor(textColor);

        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(stroke);

        fillPaint.setColor(fillColor);
    }

    public void setAudio(AudioData data) {
        audioData = data;
    }

    public void setTextPadding(int spacing) {
        paddingText = spacing;
    }

    public void setWavePadding(int padding) {
        paddingWave = padding;
    }

    public void setStepMarkerHeight(int height) {
        stepMarkerHeight = height;
    }

    public void setMiniMarkerHeight(int height) {
        miniMarkerHeight = height;
    }

    // Whole unit (from marker to marker considered as 1) weight specifies
    // mini marker count if we pass 3 means we must draw 2 mini markers inside two major markers
    // |--------|
    // |--|--|--|
    public void setMarkerSeparatorWeight(int weight) {
        miniMarkerCount = weight - 1;
        miniMarkerDiffCoef = 1 / (float) weight;
    }

    public void setStepDesiredTimeInMillis(int time) {
        stepInMillis = time;
    }

    public void setDesiredStepLength(int length) {
        stepLength = length;
    }

    public void setFontSize(float size) {
        textPaint.setTextSize(size);
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setWaveHeight(int height) {
        waveHeight = height;
    }

    public void setFormatter(AudioTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void setSidePadding(int padding) {
        paddingSide = padding;
    }

    @Override
    public int getCalculatedStepLength() {
        return stepLength;
    }

    @Override
    public int getCalculatedStepTime() {
        return stepInMillis;
    }

    @Override
    public float getCalculatedVerticalPadding() {
        return startYForTopLine;
    }

    @Override
    public Bitmap provideWaveBitmap() {
        calculate();
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Wave image width is " + finalWidth + ", height is " + finalHeight);
        }

        Bitmap bitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.RGB_565);
        bitmap.eraseColor(Color.BLACK);
        Canvas canvas = new Canvas(bitmap);

        drawWave(canvas);
        drawAxis(canvas);

        return bitmap;
    }

    private void calculate() {
        if (formatter == null) {
            throw new RuntimeException("String form must be specified");
        }

        if (audioData == null) {
            throw new RuntimeException("Audio data must be specified");
        }

        float msInPx = stepLength / (float) stepInMillis;

//      Rect bounds = new Rect();
//      mTextPaint.getTextBounds(timeFormatShort, 0, timeFormatShort.length(), bounds);
        float textHeight = textPaint.getTextSize();
        float textWidth = textPaint.measureText(formatter.getFormatStringSample());

        // Calculate spacing between to major markers in pixels
        if (stepLength < textWidth + 2 * paddingText) {
            stepLength = (int) (textWidth + 2 * paddingText);
            msInPx = stepLength / (float) stepInMillis;
        }

        if (msInPx > 0.8f) {
            throw new RuntimeException("Font size and marker spacing are incompatible!!!");
        }

        textHeight = Math.max(stepMarkerHeight, textHeight);
        centerTextY = textHeight + paddingText;
        startYForTopLine = centerTextY + miniMarkerHeight;
        startYForWave = startYForTopLine + paddingWave;
        startYForBottomLine = startYForWave + waveHeight + paddingWave;

        waveWidth = (int) (audioData.audioLength * msInPx);
        finalWidth = waveWidth + 2 * paddingSide;
        finalHeight = (int) (startYForBottomLine + startYForTopLine);
    }

    private void drawAxis(Canvas canvas) {
        // Top line
        canvas.drawRect(0, startYForTopLine - lineWidth,
                finalWidth, startYForTopLine + lineWidth, fillPaint);

        int milliSeconds = 0;
        float majorBarTopY = startYForTopLine - stepMarkerHeight;
        float minorBarTopY = startYForTopLine - miniMarkerHeight;
        float miniMarkerLength = stepLength * miniMarkerDiffCoef;

        // Bars should be drawn in 3 steps:
        // 1. Draw axis and labels according to wave.
        // 2. Draw left from wave start.
        // 3. Draw right from wave end.

        // Draw texts
        canvas.save();
        canvas.translate(paddingSide, 0);

        float waveMajorMarkerX;
        for (waveMajorMarkerX = 0; waveMajorMarkerX < waveWidth; waveMajorMarkerX += stepLength) {
            float sectionStart = waveMajorMarkerX;

            // Draw major line
            canvas.drawRect(sectionStart - lineWidth, startYForTopLine,
                    sectionStart + lineWidth, majorBarTopY, fillPaint);

            float miniMarkerPosX = 0;
            for (int j = 0; j < miniMarkerCount; ++j) {
                miniMarkerPosX += miniMarkerLength;
                float miniMarkerX = sectionStart + miniMarkerPosX;
                canvas.drawRect(miniMarkerX - lineWidth,
                        startYForTopLine, miniMarkerX + lineWidth, minorBarTopY, fillPaint);
            }

            canvas.drawText(formatter.formatTime(milliSeconds),
                    sectionStart + stepLength / 2, centerTextY - paddingText, textPaint);

            milliSeconds += stepInMillis;
        }

        // Draw last major line
        canvas.drawRect(waveMajorMarkerX - lineWidth, startYForTopLine,
                waveMajorMarkerX + lineWidth, majorBarTopY, fillPaint);

        canvas.restore();

        // Draw bars left from waves.
        for (float i = paddingSide; i >= 0; i -= stepLength) {
            float sectionStart = i;

            // Draw major line
            canvas.drawRect(sectionStart - lineWidth, startYForTopLine,
                    sectionStart + lineWidth, majorBarTopY, fillPaint);

            float miniMarkerPosX = 0;
            for (int j = 0; j < miniMarkerCount; ++j) {
                miniMarkerPosX += miniMarkerLength;
                float miniMarkerX = sectionStart - miniMarkerPosX;
                canvas.drawRect(miniMarkerX - lineWidth,
                        startYForTopLine, miniMarkerX + lineWidth, minorBarTopY, fillPaint);
            }
        }

        // Draw bars right from waves.
        for (float i = waveMajorMarkerX + paddingSide; i <= finalWidth; i += stepLength) {
            float sectionStart = i;

            // Draw major line
            canvas.drawRect(sectionStart - lineWidth, startYForTopLine,
                    sectionStart + lineWidth, majorBarTopY, fillPaint);

            float miniMarkerPosX = 0;
            for (int j = 0; j < miniMarkerCount; ++j) {
                miniMarkerPosX += miniMarkerLength;
                float miniMarkerX = sectionStart + miniMarkerPosX;
                canvas.drawRect(miniMarkerX - lineWidth,
                        startYForTopLine, miniMarkerX + lineWidth, minorBarTopY, fillPaint);
            }
        }

        // Bottom line
        canvas.drawRect(0, startYForBottomLine - lineWidth,
                finalWidth, startYForBottomLine + lineWidth, fillPaint);
    }

    private void drawWave(Canvas canvas) {
        canvas.save();
        canvas.translate(paddingSide, startYForWave);
        Path mWaveform = drawPlaybackWaveform(waveWidth, waveHeight, audioData.samples);
        canvas.drawPath(mWaveform, fillPaint);
        canvas.drawPath(mWaveform, strokePaint);

        // Draw middle line
        float middleLineWidth = 2 * lineWidth;
        canvas.drawRect(-paddingSide, waveHeight / 2 - middleLineWidth,
                finalWidth, waveHeight / 2 + middleLineWidth, fillPaint);

        canvas.restore();
    }

    private static Path drawPlaybackWaveform(int width, int height, short[] buffer) {
        Path waveformPath = new Path();
        float centerY = height / 2f;

        WaveData waves = WaveDataExtractor.getExtremes(buffer, width, 1);

        float max = waves.maxValue;
        short[][] extremes = waves.extremes;

        waveformPath.moveTo(0, centerY);

        // draw maximums
        for (int x = 0; x < width; x++) {
            short sample = extremes[x][0];
            float y = centerY - ((sample / max) * centerY);
            waveformPath.lineTo(x, y);
        }

        // draw minimums
        for (int x = width - 1; x >= 0; x--) {
            short sample = extremes[x][1];
            float y = centerY - ((sample / max) * centerY);
            waveformPath.lineTo(x, y);
        }

        waveformPath.close();

        return waveformPath;
    }
}
