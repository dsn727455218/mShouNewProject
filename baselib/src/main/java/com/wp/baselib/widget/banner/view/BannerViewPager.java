package com.wp.baselib.widget.banner.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class BannerViewPager extends ViewPager {
    private boolean scrollable = true;
    private float mMDownX;
    private float mMDownY;

    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return this.scrollable && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
         int action=ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mMDownX = ev.getX();
                mMDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = Math.abs(mMDownX - ev.getX());
                float offsetY = Math.abs(mMDownY - ev.getX());
                if (offsetX > offsetY) {
                    return true;
                }
                break;
        }

        return this.scrollable && super.onInterceptTouchEvent(ev);
    }
    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }
}
