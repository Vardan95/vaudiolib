package com.vpetrosyan.audio.vwaveview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
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
        void onSeekStarted();

        void onSeek(long time);

        void onSeekCompleted();
    }

    private WaveImageProvider provider;
    //    private ImageView imageView;
//    private ScrollReporterHorizontalScrollView scrollView;
    private View sliderView;
    private Bitmap bitmap;

    private boolean hasAudio = false;

    private int sliderLineWidth;

    private int stepDesiredLength;
    private int stepDesiredTimeInMs;

    private int waveImageWidth;

    private SeekListener listener;

    private long currentSeekTime = 0;
    private int currentScrollPosition = 0;

    private RecyclerView waveScrollView;
    private WaveImageViewAdapter waveImageAdapter;
    private LinearLayoutManager layoutManager;

    private boolean isUserIntercepted;

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

    public int getPosition(long time) {
        return (int) ((time / (float) provider.getCalculatedStepTime()) * provider.getCalculatedStepLength());
    }

    public void seekTo(int timeInMillis) {
        if (hasAudio) {
            isUserIntercepted = false;
            int currentX = getPosition(currentSeekTime);
            int desX = getPosition(timeInMillis);
            int diff = desX - currentX;
            if(diff != 0) {
                waveScrollView.smoothScrollBy(diff, 0);
            }
        }
    }


    public void setListener(SeekListener listener) {
        this.listener = listener;
    }

    private void init() {

        waveScrollView = new RecyclerView(getContext());
        waveImageAdapter = new WaveImageViewAdapter();

        layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);

        waveScrollView.setLayoutManager(layoutManager);

        waveScrollView.setBackgroundColor(Color.BLACK);

        waveScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    listener.onSeekStarted();
                    isUserIntercepted = true;
                }
                return false;
            }
        });

        waveScrollView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(isUserIntercepted) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        isUserIntercepted = false;
                        if (listener != null) {
                            listener.onSeekCompleted();
                        }
                    }
                }
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                currentScrollPosition += dx;

                if (currentScrollPosition > waveImageWidth) {
                    currentScrollPosition = waveImageWidth;
                }

                if (currentScrollPosition < 0) {
                    currentScrollPosition = 0;
                }

                currentSeekTime = (long) ((currentScrollPosition / (float) provider.getCalculatedStepLength()) * provider.getCalculatedStepTime());

                if(isUserIntercepted) {
                    listener.onSeek(currentSeekTime);
                }
            }
        });

        waveScrollView.setAdapter(waveImageAdapter);

        addView(waveScrollView);


//        imageView = new ImageView(getContext());
//
//        scrollView = new ScrollReporterHorizontalScrollView(getContext());
//        scrollView.setListener(this);
        sliderView = new View(getContext());
//
//        scrollView.setFillViewport(true);
//        scrollView.setSmoothScrollingEnabled(true);
//        scrollView.setBackgroundColor(COLOR_BACKGROUND);
//
//        FrameLayout.LayoutParams scrollViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        addView(scrollView, scrollViewParams);
//
//        scrollView.addView(imageView);
//
//        // TODO(Vardan) Change to match specs.
        sliderLineWidth = SizeUtils.convertDpToPixels(5, getContext());
        sliderView.setBackgroundColor(Color.RED);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(sliderLineWidth, -1);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addView(sliderView, params);

        stepDesiredLength = SizeUtils.convertDpToPixels(DEFAULT_STEP_SIZE, getContext());
        stepDesiredTimeInMs = DEFAULT_STEP_TIME_LENGTH;
    }

    public void setAudio(AudioData data) {
        hasAudio = true;

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Rate is " + data.sampleRate + ", channels are " + data.channels +
                    ", length is " + data.audioLength);
        }

        provider = createNativeProvider(data);

        bitmap = provider.provideWaveBitmap();
        waveImageAdapter.setImage(bitmap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (hasAudio) {
            int width = getMeasuredWidth() - getPaddingRight() - getPaddingRight();
            provider.setSidePadding(width / 2);

            bitmap.recycle();

            long start = System.currentTimeMillis();

            bitmap = provider.provideWaveBitmap();

            waveImageWidth = bitmap.getWidth();
            updateSliderHeight(bitmap.getHeight());

            waveImageAdapter.setImage(bitmap);

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Drawing took " + (System.currentTimeMillis() - start));
            }

        }
    }

    @Override
    public void onScrollUpdated(int x, int y) {
        if (hasAudio) {
            if (x > waveImageWidth) {
                x = waveImageWidth;
            }

            if (x < 0) {
                x = 0;
            }

            currentSeekTime = (long) ((x / (float) provider.getCalculatedStepLength()) * provider.getCalculatedStepTime());
        }
    }

    @Override
    public void onScrollStart() {
        if (listener != null) {
            listener.onSeekStarted();
        }
    }

    @Override
    public void onScrollEnd() {
        if (listener != null) {
            listener.onSeek(currentSeekTime);
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