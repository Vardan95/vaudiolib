package com.vpetrosyan.audio.vwaveview;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
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

    private GestureDetector mDetector = new GestureDetector(new MyGestureListener());

    public void setListener(ScrollUpdateListener listener) {
        this.listener = listener;
    }

    public ScrollReporterHorizontalScrollView(Context context) {
        super(context);
    }

    private boolean isFling = false;

    public boolean isFromTouch = true;

    public void scrollTo(int pos) {
        isFromTouch = false;
        smoothScrollTo(pos, 0);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

//        if(!isFromTouch) {
//            return;
//        }

//        if(isFromTouch){
//            return;
//        }

        Log.e("Vardan", " onScrollChanged " + isFling);

        if(listener != null) {
            listener.onScrollUpdated(l, t);
        }
//
        if(isFling) {
            if (listener != null) {
                listener.onScrollEnd();
            }
        }

//        if(listener != null) {
//
//
//            if(isFling) {
//                if(listener != null) {
//                    Log.e("Vardan", " onScrollChanged was fling");
//                }
//            }
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                startClickTime = System.currentTimeMillis();
                if(listener != null)
                    listener.onScrollStart();
//                isFromTouch = true;
//                isFling = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e("Vardan", "ACTION_CANCEL");
            case MotionEvent.ACTION_UP:
                Log.e("Vardan", "ACTION_UP");
//                if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
//                    Log.e("Vardan", "first");
//                } else {
//                    Log.e("Vardan", "second");
//                    if(listener != null)
//                        listener.onScrollEnd();
//                    Log.e("Vardan", "was scroll");
//                }
                if(listener != null)
                    listener.onScrollEnd();
                break;
        }

        return true;
    }

    //    @Override
//    public void fling(int velocityX) {
//        super.fling(velocityX);
//        isFling = true;
//        Log.e("Vardan", "fling velocity " + velocityX);
//    }
//
//    private long startClickTime;
//
//

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                startClickTime = System.currentTimeMillis();
//                if(listener != null)
//                    listener.onScrollStart();
//                isFromTouch = true;
//                isFling = false;
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                Log.e("Vardan", "ACTION_CANCEL");
//            case MotionEvent.ACTION_UP:
//                Log.e("Vardan", "ACTION_UP");
//                if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
//                    Log.e("Vardan", "first");
//                } else {
//                    Log.e("Vardan", "second");
//                    if(listener != null)
//                        listener.onScrollEnd();
//                    Log.e("Vardan", "was scroll");
//                }
//
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");
            if(listener != null) {
                listener.onScrollStart();
            }
            isFromTouch = true;
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("TAG", "onScroll: ");
            isFling = false;
            ScrollReporterHorizontalScrollView.this.scrollBy(-(int) distanceX, 0);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.i("TAG", "onFling: ");
            isFling = true;
            ScrollReporterHorizontalScrollView.this.fling(-(int) velocityX);
            return true;
        }
    }

}
