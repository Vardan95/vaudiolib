package com.vpetrosyan.audio.vwaveview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;

import com.vpetrosyan.audio.utils.SizeUtils;

/**
 * Created by varan on 2/26/18.
 */

public class SliderView extends View {

    private static final int DEFAULT_LINE_WIDTH = 1; //dp
    private static final int DEFAULT_CIRCLE_RADIUS = 3; //dp
    private static final int DEFAULT_LINE_COLOR = Color.parseColor("#194fe7");
    private static final int DEFAULT_CIRCLE_COLOR = Color.parseColor("#ea6927");

    private int sliderLineWidth;
    private int sliderCircleRadius;

    private Paint sliderCirclePaint;
    private Paint sliderLinePaint;

    private Bitmap sliderImage;

    public SliderView(Context context) {
        super(context);

        sliderLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sliderLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        sliderLinePaint.setAntiAlias(true);
        sliderLinePaint.setColor(DEFAULT_LINE_COLOR);

        sliderCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sliderCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        sliderCirclePaint.setAntiAlias(true);
        sliderCirclePaint.setColor(DEFAULT_CIRCLE_COLOR);

        sliderLineWidth = SizeUtils.convertDpToPixels(DEFAULT_LINE_WIDTH, getContext());
        sliderCircleRadius = SizeUtils.convertDpToPixels(DEFAULT_CIRCLE_RADIUS, getContext());

        setDrawingCacheEnabled(true);
    }

    public void updateSlider(int height, int padding) {

        if(sliderImage != null) {
            sliderImage.recycle();
        }

        int bitmapWidth = sliderCircleRadius > sliderLineWidth / 2f ?
                2 * sliderCircleRadius : sliderLineWidth;

        sliderImage = Bitmap.createBitmap(bitmapWidth, height, Bitmap.Config.ARGB_8888);
        sliderImage.eraseColor(Color.TRANSPARENT);

        float centerX = bitmapWidth / 2f;

        Canvas canvas = new Canvas(sliderImage);
        canvas.drawRect(centerX - sliderLineWidth / 2f , padding,
                centerX + sliderLineWidth / 2f, height - padding, sliderLinePaint);

        canvas.drawCircle(centerX, padding, sliderCircleRadius, sliderCirclePaint);
        canvas.drawCircle(centerX, height - padding, sliderCircleRadius, sliderCirclePaint);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.height = height;
        params.width = bitmapWidth;

        setLayoutParams(params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(sliderImage != null) {
            canvas.drawBitmap(sliderImage, 0, 0, null);
        }
    }
}
