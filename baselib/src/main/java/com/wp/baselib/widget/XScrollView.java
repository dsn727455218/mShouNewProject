package com.wp.baselib.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * 增加滚动到底部或顶部监听器的滚动视图
 * @description
 * @author summer
 * @date 2014年10月31日 上午11:49:48
 *
 */
public class XScrollView extends ScrollView {
    private static final long DELAY = 100;

    private int currentScroll;
    private Runnable scrollCheckTask;
    //第一次
    private boolean once;
    /**
     * @param context
     */
    public XScrollView(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public XScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public XScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        scrollCheckTask = new Runnable() {
            @Override
            public void run() {
                int newScroll = getScrollY();
                if (currentScroll == newScroll) {
                    if (onScrollListener != null) {
                    	onScrollListener.onScrollStopped(getScrollX(),getScrollY());
                    }
                } else {
                    if (onScrollListener != null) {
                        onScrollListener.onScrolling();
                    }
                    currentScroll = getScrollY();
                    postDelayed(scrollCheckTask, DELAY);
                }
            }
        };
        
        setOnTouchListener(new OnTouchListener() {
        	
           

			@Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					once = false;
					currentScroll = getScrollY();
	                postDelayed(scrollCheckTask, DELAY);
					break;
				}
                return false;
            }
        });
    }

    /**
     * 设置监听器
     * @description
     * @author summer
     * @date 2014年10月31日 上午11:50:57
     *
     */
    public interface OnScrollListener {
    	/**
         * 滚动发生变化调用
         */
        void onScrollChanged(int x, int y, int oldX, int oldY);

        /**
         * 滚动停止时或移动不动时调用,当前x，y坐标
         */
        void onScrollStopped(int x, int y);
        
        /**
         * 在滚动中时调用
         */
        void onScrolling();
        
        /**
         * 无法滚动时移动的时候调用 
         * @param isTop true在顶部,否则底部
         */
        void onNoScrollMove(boolean isTop);
    }

    private OnScrollListener onScrollListener;

    /**
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(x, y, oldX, oldY);
        }
    }
    
    @Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
			boolean clampedY) {
		if (!once) {
			if (clampedY) {//滚动顶部或底部
				if (scrollY==0) { //顶部
					 if (onScrollListener != null) {
	                        onScrollListener.onNoScrollMove(true);
	                 }
				} else { //底部
					 if (onScrollListener != null) {
	                        onScrollListener.onNoScrollMove(false);
	                 }
				}
			}
			once = true;
		}
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

	/**
     * 子视图是否显示
     * @param child
     * @return
     */
    public boolean isChildVisible(View child) {
        if (child == null) {
            return false;
        }
        Rect scrollBounds = new Rect();
        getHitRect(scrollBounds);
        return child.getLocalVisibleRect(scrollBounds);
    }

    /**
     * 是否在头部
     * @return
     */
    public boolean isAtTop() {
        return getScrollY() <= 0;
    }

    /**
     * 是否在底部
     * @return
     */
    public boolean isAtBottom() {
        return getChildAt(getChildCount() - 1).getBottom() + getPaddingBottom() == getHeight() + getScrollY();
    }
    
    /**
     * 获取当前滚动到子视图位置，以最后一个显示位置为准
     * @param currentScreenHeight 当前屏幕高度
     * @return
     */
    public int getCurrentChildViewPosition(int currentScreenHeight) 
    {
    	int index = 0;
    	if (getChildCount()<0)
    		return index;
    	ViewGroup ll = (ViewGroup) getChildAt(0);
		int count = ll.getChildCount();
		int tempHeight=0;
		for (int i = 0; i < count; i++) {
			View mView = ll.getChildAt(i);
			tempHeight += mView.getMeasuredHeight();
			if ( tempHeight >= getScrollY() + currentScreenHeight) {
				index = i;
				break;
			}
		}
		return index;
    }

}
