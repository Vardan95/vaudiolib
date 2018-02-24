package com.vpetrosyan.audio.vwaveview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.vpetrosyan.audio.file.AudioData;
import com.vpetrosyan.audio.formatter.AudioTimeFormatter;
import com.vpetrosyan.audio.formatter.ShortTimeFormatter;
import com.vpetrosyan.audio.utils.SizeUtils;
import com.vpetrosyan.audio.wave.NativeWaveImageProvider;
import com.vpetrosyan.audio.wave.WaveImageProvider;

/**
 * Created by varan on 2/24/18.
 * NOTE!! Use large heap for application.
 */
public class VWaveView extends FrameLayout implements
        ScrollReporterHorizontalScrollView.ScrollUpdateListener {
    private static final String TAG = VWaveView.class.getSimpleName();
    private static final int COLOR_BACKGROUND = Color.BLACK;

    // All values are in dp, sp and milliseconds.
    private static final int DEFAULT_STEP_SIZE = 10;
    private static final int DEFAULT_STEP_TIME_LENGTH = 10000;
    private static final int DEFAULT_FONT_SIZE = 12; //sp
    private static final int DEFAULT_WAVE_HEIGHT = 130; //dp
    private static final int DEFAULT_TEXT_PADDING = 5; //dp
    private static final int DEFAULT_WAVE_PADDING = 5; //dp
    private static final int DEFAULT_STEP_MARKER_HEIGHT = 10; //dp
    private static final int DEFAULT_MINI_MARKER_HEIGHT = 5; //dp

    private static final int DEFAULT_MINI_MARKER_WEIGHT = 4;
    private static final AudioTimeFormatter DEFAULT_FORMATTER = new ShortTimeFormatter();

    public interface SeekListener {
        void onSeek(long time, boolean isFromUser);
    }

    private WaveImageProvider provider;
    private ImageView imageView;
    private ScrollReporterHorizontalScrollView scrollView;
    private View sliderView;
    private Bitmap bitmap;

    private boolean hasAudio = false;

    private int sliderLineWidth;

    private int stepDesiredLength;
    private int stepDesiredTimeInMs;

    private int waveImageWidth;

    private SeekListener listener;

    private boolean isUserIntercepted = true;

    public VWaveView(Context context) {
        super(context);
        init();
    }

    public VWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void seekTo(int timeInMillis) {
        if(hasAudio) {
            int x = (int) ((timeInMillis / (float) provider.getCalculatedStepTime()) * provider.getCalculatedStepLength());
            isUserIntercepted = false;
            scrollView.smoothScrollTo(x, 0);
            isUserIntercepted = true;
        }
    }

    public void setListener(SeekListener listener) {
        this.listener = listener;
    }

    private void init() {
        imageView = new ImageView(getContext());

        scrollView = new ScrollReporterHorizontalScrollView(getContext());
        scrollView.setListener(this);
        sliderView = new View(getContext());

        scrollView.setFillViewport(true);
        scrollView.setSmoothScrollingEnabled(true);
        scrollView.setBackgroundColor(COLOR_BACKGROUND);

        FrameLayout.LayoutParams scrollViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(scrollView, scrollViewParams);

        scrollView.addView(imageView);

        // TODO(Vardan) Change to match specs.
        sliderLineWidth = SizeUtils.convertDpToPixels(5, getContext());
        sliderView.setBackgroundColor(Color.RED);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(sliderLineWidth, -1);
        params.gravity = Gravity.CENTER_HORIZONTAL;
//
        addView(sliderView, params);

        stepDesiredLength = SizeUtils.convertDpToPixels(DEFAULT_STEP_SIZE, getContext());
        stepDesiredTimeInMs = DEFAULT_STEP_TIME_LENGTH;

        setWillNotDraw(false);
    }

    public void setAudio(AudioData data) {
        hasAudio = true;

        if(BuildConfig.DEBUG) {
            Log.e(TAG, "Rate is " + data.sampleRate + ", channels are " + data.channels +
                        ", length is " + data.audioLength);
        }

        provider = createNativeProvider(data);

        bitmap = provider.provideWaveBitmap();
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(hasAudio) {
            int width = getMeasuredWidth() - getPaddingRight()- getPaddingRight();
            provider.setSidePadding(width / 2);

            bitmap.recycle();

            long start = System.currentTimeMillis();

            bitmap = provider.provideWaveBitmap();

            waveImageWidth = bitmap.getWidth();
            updateSliderHeight(bitmap.getHeight());

            imageView.setImageBitmap(bitmap);

            if(BuildConfig.DEBUG) {
                Log.e(TAG, "Drawing took " + (System.currentTimeMillis() - start));
            }

        }
    }

    @Override
    public void onScrollUpdated(int x, int y) {
        if(hasAudio) {
            if (x > waveImageWidth) {
                x = waveImageWidth;
            }

            if(x < 0) {
                x = 0;
            }

            long time = (long) ((x / (float) provider.getCalculatedStepLength()) * provider.getCalculatedStepTime());

            if(BuildConfig.DEBUG) {
                Log.e(TAG, "Selected time is " + time);
            }

            if(listener != null) {
                listener.onSeek(time, isUserIntercepted);
            }
        }
    }

    private void updateSliderHeight(int height) {
        LayoutParams params = (LayoutParams) sliderView.getLayoutParams();
        params.height = height;
        sliderView.setLayoutParams(params);
    }

    private WaveImageProvider createNativeProvider(AudioData data) {
        Context context = getContext();

        NativeWaveImageProvider provider = new NativeWaveImageProvider();
        provider.setAudio(data);
        provider.setStepDesiredTimeInMillis(stepDesiredTimeInMs);
        provider.setDesiredStepLength(stepDesiredLength);
        provider.setFontSize(SizeUtils.convertSpToPixels(DEFAULT_FONT_SIZE, context));
        provider.setWaveHeight(SizeUtils.convertDpToPixels(DEFAULT_WAVE_HEIGHT, context));
        provider.setMarkerSeparatorWeight(DEFAULT_MINI_MARKER_WEIGHT);
        provider.setSidePadding(0);
        provider.setFormatter(DEFAULT_FORMATTER);
        provider.setTextPadding(SizeUtils.convertDpToPixels(DEFAULT_TEXT_PADDING, context));
        provider.setWavePadding(SizeUtils.convertDpToPixels(DEFAULT_WAVE_PADDING, context));
        provider.setStepMarkerHeight(SizeUtils.convertDpToPixels(DEFAULT_STEP_MARKER_HEIGHT, context));
        provider.setMiniMarkerHeight(SizeUtils.convertDpToPixels(DEFAULT_MINI_MARKER_HEIGHT, context));

        return provider;
    }
}