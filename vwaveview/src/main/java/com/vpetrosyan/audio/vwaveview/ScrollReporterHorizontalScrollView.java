package com.vpetrosyan.audio.vwaveview;

import android.content.Context;
import android.widget.HorizontalScrollView;

/**
 * Created by varan on 2/24/18.
 */

class ScrollReporterHorizontalScrollView extends HorizontalScrollView {

    interface ScrollUpdateListener {
        void onScrollUpdated(int x, int y);
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
}
