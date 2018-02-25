package com.vpetrosyan.audio.vwaveview;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by varan on 2/24/18.
 */

class ScrollReporterHorizontalScrollView extends HorizontalScrollView {

    interface ScrollUpdateListener {
        void onScrollStart();
        void onScrollUpdated(int x, int y);
        void onScrollEnd();
    }

    private ScrollUpdateListener listener;

    public void setListener(ScrollUpdateListener listener) {
        this.listener = listener;
    }

    public ScrollReporterHorizontalScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if(listener != null) {
            listener.onScrollUpdated(l, t);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(listener != null)
                    listener.onScrollStart();
                break;
            case MotionEvent.ACTION_UP:
                if(listener != null)
                    listener.onScrollEnd();
                break;
        }
        return super.onTouchEvent(event);
    }

}
